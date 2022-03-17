package social.tsu.plugins

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import social.tsu.model.*
import social.tsu.model.data.*
import social.tsu.model.repo.TriviaSerializer
import social.tsu.test.reportError
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

val serializer = TriviaSerializer()
var gameId: Int = 1001

val json = Json {
    encodeDefaults = true
}

internal class GameContext(
    val testData: TriviaTestData
) {
    val subscribed = AtomicBoolean(false)
    val questionActive = AtomicBoolean(false)
    val surveyState = AtomicInteger(SurveyState.STOPPED.ordinal)
    val job = AtomicReference<Job?>(null)

    val gameId: Int
        get() = testData.gameId
}

internal val games = listOf(
    GameContext(
        TriviaTestData(
            gameId = 1001,
            gameType = SurveyType.POLL
        )
    ),
    GameContext(
        TriviaTestData(
            gameId = 1002,
            gameType = SurveyType.TRIVIUM
        )
    )
)

internal fun findGame( gameId: Int): GameContext? {
    return games.firstOrNull { it.testData.gameId == gameId }
}

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/") { // websocketSession

            suspend fun protocolError( msg: String) {
                println("Protocol error, msg=$msg")
                close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR,"Protocol error: $msg"))
            }

            // reset the games
            games.forEach { gc ->
                gc.surveyState.set( SurveyState.STOPPED.ordinal)
                gc.subscribed.set(false)
                gc.job.set(null)
                gc.questionActive.set(false)
            }

            launch {
                // look for remote close
                closeReason.await()?.let { println("remote close, reason=$it") }
            }

            println("----- socket opened")
            send(json.encodeToString(TriviaTestData.connectionResponse))

            delay(100)
            launchGamesListSender()
            val responseJob = launchResponseHandler()
            startNextGame()

            println("stuff is running")
            responseJob.join()
        }
    }
}

fun Frame.decode(): Any? {
    return when (this) {
        is Frame.Text -> {
            val text = readText()
            serializer.decodeString(text).apply {
                println("received msg=$this")
            }
        }

        is Frame.Close -> {
            // return ReasonCode for close
            this.readReason()
        }

        else -> {
            println("---- Unrecognized frame type: ${this.frameType}")
            null
        }
    }
}

internal fun WebSocketSession.startNextGame(): Job =
    launch {
        delay(5000)
        games.firstOrNull { it.surveyState.get() == SurveyState.STOPPED.ordinal }?.let { gc ->
            gc.surveyState.set( SurveyState.STARTED.ordinal)
        }?:let {
            println("-----> All Done <------")
            close( CloseReason(CloseReason.Codes.NORMAL, "All done") )
        }
    }

internal fun WebSocketSession.launchGame(gameContext: GameContext): Job =
    launch {

        try {
            val surveyMsg = json.encodeToString(gameContext.testData.survey)
            repeat(3) {
                if (!gameContext.subscribed.get()) return@launch
                send(surveyMsg)
                delay(gameContext.testData.gameMessagePeriodMsec)
            }

            (0 until gameContext.testData.numQuestions).forEach {  qNum ->
                val sr = gameContext.testData.getSurveyResults(qNum, false) ?: return@forEach
                println("sending question: ${sr.questionsList.firstOrNull()?.questionTitle}")
                val srMsg = json.encodeToString(sr)
                gameContext.questionActive.set(true)
                repeat(2) {
                    if (!gameContext.subscribed.get()) return@launch
                    send(surveyMsg)
                    delay(50)
                    send(srMsg)
                    delay(gameContext.testData.gameMessagePeriodMsec)
                }
                println("answer window closed")
                gameContext.questionActive.set(false) // stop accepting answers
                repeat(1) {
                    if (!gameContext.subscribed.get()) return@launch
                    send(surveyMsg)
                    delay(50)
                    send(srMsg)
                    delay(gameContext.testData.gameMessagePeriodMsec)
                }

                // Send correct answer message
                val asr = gameContext.testData.getSurveyResults(qNum, true) ?: return@forEach
                val asrJson = json.encodeToString(asr)

                repeat(3) {
                    if (!gameContext.subscribed.get()) return@launch
                    send(surveyMsg)
                    send( asrJson )
                    delay(gameContext.testData.gameMessagePeriodMsec)
                    // TODO make end game message change to reflect updated totals
                }

            }
            println("done w/ questions")
            gameContext.surveyState.set(SurveyState.COMPLETED.ordinal)
            val endGameMsg = json.encodeToString( gameContext.testData.endGameResults )
            repeat(20) {
                if (!gameContext.subscribed.get()) return@launch
                send(surveyMsg)
                send(endGameMsg)
                delay(gameContext.testData.gameMessagePeriodMsec)
            }

            println("--- results done")
            delay(1000)
        } catch (e: Exception) {
            println("Exception during game: ${e.javaClass.simpleName}  ${e.message}")
        }
    }

internal fun WebSocketSession.launchGamesListSender(): Job =
    launch {
        while(isActive){
            send( json.encodeToString(createGamesListMsg()))
            delay(3000L)
        }
    }

internal fun WebSocketSession.launchResponseHandler(): Job =
    launch {
        for (frame in incoming) {
            when(val ansrResp = frame.decode()) {
                is WsTriviaAnswerResponse -> {
                     ansrResp.answers.firstOrNull()?.answer?.let { ansr ->
                        println("User answered: answerId=${ansr.answerId}")
                         delay(500)
                         val gc = findGame(ansrResp.gameId)
                         val resp = createAnswerAcknowledgement(ansr.questionId, ansr.answerId, gc?.questionActive?.get() ?: false)
                         send( json.encodeToString(resp))
                    }
                }

                is WsTriviaGameSubscription -> {
                    findGame(ansrResp.gameId)?.let { gc ->
                        when (val currentState = gc.surveyState.get() ){
                            SurveyState.STARTED.ordinal -> {
                                val was = gc.subscribed.getAndSet(true)
                                if (was)
                                    reportError("Game already subscribed")
                                else {
                                    gc.job.getAndSet(null)?.cancelAndJoin()
                                    println("Launching game: ${gc.gameId}")
                                    gc.job.set(launchGame(gc))
                                }
                            }

                            else -> reportError("subscribe message received w/ game in wrong state: $currentState")
                        }
                    }
                }

                is WsTriviaGameUnsubscription -> {
                    findGame(ansrResp.gameId)?.let { gc ->
                        val was = gc.subscribed.getAndSet(false)
                        println("Unsubscribing from game ${gc.gameId}, was subscribed: ${was}")
                        startNextGame()
                    } ?: let {
                        reportError("unrecognized gameId in unsubscribe: ${ansrResp.gameId}")
                    }
                }

                is CloseReason -> {
                    close()
                    return@launch
                }
            }
        }
    }


internal fun createGamesListMsg(): WsGamesInfoList {

    return WsGamesInfoList(
        data = games.map { gc ->
            GameInfo(
                id = gc.testData.gameId,
                title = "Test ${gc.testData.gameType.name}",
                startTime = "2022-03-15T15:00:00Z",
                surveyType = gc.testData.gameType.ordinal,
                gameStatus = gc.surveyState.get()
            )
        }
    )
}