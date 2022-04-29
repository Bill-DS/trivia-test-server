package social.tsu.model

import social.tsu.android.network.model.trivia.websocket.WsAnswerResults
import social.tsu.android.network.model.trivia.websocket.WsEndGameResults
import social.tsu.android.network.model.trivia.websocket.WsQuestionResults
import social.tsu.android.network.model.trivia.websocket.WsSurveyResults
import social.tsu.model.data.WsSurvey
import social.tsu.model.data.WsTriviaConnected
import social.tsu.model.data.sq
import social.tsu.plugins.GameContext


class TriviaTestData(
    val gameId:Int = 1234,
    val gameType: SurveyType = SurveyType.TRIVIUM
){
    companion object {
        val networkDelayMsec: Long = 600L  // 600 ms is typical for geo satellite internaet
        val connectionResponse = WsTriviaConnected(
            id = "1234",
            status = "OK",
            channels = listOf( "TRIVIA" )
        )
    }

    val gameMessagePeriodMsec: Long = 1_000L
    val questionDisplayedLoops = 4
    val questionClosedLoops = 3
    val answerDisplayedLoops = 6


    val sampleQuestions = when(gameType) {
        SurveyType.TRIVIUM ->
            listOf(
                sq("What is your favorite color?", 0, "Red", "Green", "Blue", "Yellow"),
                sq("What is your quest?", 1, "World peace", "The holy grail", "A good nap", "A new couch"),
                sq("What is the airspeed velocity of an unladen swallow?", 2, "60 mph", "2 furlongs/fortnight", "European or African?", "11"),
                sq( "What shall thou count for the Holy Hand Grenade of Antioch?", 1, "2", "3", "4", "5"),
//                sq("What is your favorite color?", 0, "Red", "Green", "Blue", "Yellow"),
//                sq("What is your quest?", 1, "World peace", "The holy grail", "A good nap", "A new couch"),
//                sq("What is the airspeed velocity of an unladen swallow?", 2, "60 mph", "2 furlongs/fortnight", "European or African?", "11"),
//                sq( "What shall thou count for the Holy Hand Grenade of Antioch?", 1, "2", "3", "4", "5")
            )
        SurveyType.POLL ->
            listOf(
                sq("This is a practice question, what is the color of the sky?", 1, "Green", "Blue", "Pink")
            )
    }

    val jackpot = 500.0

    val numQuestions = sampleQuestions.size

    fun getSurvey( context: GameContext ): WsSurvey {
        return WsSurvey(
            surveyId = 1,
            gameId = context.gameId,
            gameJackpot = jackpot,
            jackpotPercentBarrier = 100,
            surveyState = context.surveyState.get(),
            type = gameType.value,
            maxNumberOfQuestions = numQuestions,
            questionsList = listOf()

        )
    }
//    val survey = WsSurvey(
//        surveyId = 1,
//        gameId = gameId,
//        gameJackpot = jackpot,
//        jackpotPercentBarrier = 100,
//        surveyState = SurveyState.STARTED.ordinal,
//        type = gameType.value,
//        maxNumberOfQuestions = numQuestions,
//        questionsList = listOf()
//    )

    fun getSurveyResults(qNum: Int, answered: Boolean = false, repeat: Int=0): WsSurveyResults? =
        sampleQuestions.getOrNull(qNum)?.let { sq ->
            WsSurveyResults(
                gameId = gameId,
                questionsList = listOf(
                    WsQuestionResults(
                        questionId = qNum+100,
                        questionTitle = sq.question,
                        questionOrder = qNum+1,
                        answersList = sq.answers.mapIndexed { aNdx, aTxt ->
                            WsAnswerResults(
                                answerId = (qNum+1)*100 + aNdx + 1,
                                answerTitle = aTxt,
                                isAnswerCorrect = answered && sq.correct == aNdx && gameType != SurveyType.POLL,
                                answerPercentage = ((20+repeat).toString() + ".00").takeIf { answered } ?: "0", // BE sends "0" instead of null
                                answersCount = 5.takeIf { answered }
                            )
                        }
                    )
                )
            )
        }

    val endGameResults = WsEndGameResults(
        gameId = gameId,
        gameType = gameType.value,
        gameState = SurveyState.COMPLETED.ordinal,
        prizePool = jackpot.toFloat(),
        numberOfWinners = 123,
        share = 10.0f,
        jackpotPercentBarrier = 100
    )
}
