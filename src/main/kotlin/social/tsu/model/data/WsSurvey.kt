package social.tsu.model.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The trivia game data as returned by the web socket.
 */
@Serializable
data class WsSurvey(
    @SerialName("id") // the id of the poll / trivia
    val surveyId: Int,

    @SerialName("g_id") // game id
    val gameId: Int,

    @SerialName("j") // game jackpot
    val gameJackpot: Double,

    @SerialName("p_c") // percent of questions to qualify for jackpot
    val jackpotPercentBarrier: Int,

    @SerialName("s") // survey state - 0:stopped, 1:paused, 2:started, 3:completed
    val surveyState: Int,

    @SerialName("t") // type of item (pool/trivium/results)
    val type: String,

    @SerialName("max_q") // max number of questions
    val maxNumberOfQuestions: Int,

    @SerialName("q") // array/list of questions
    val questionsList: List<WsQuestion>

) : WebsocketTrivia

@Serializable
data class WsQuestion(
    @SerialName("id") // id of question
    val questionId: Int,
    @SerialName("nth") // position of question based on max above
    val questionNumber: Int,
    @SerialName("time") // how long in seconds the answers are accepted for // max interval (seconds) for question
    val answerAcceptedTime: Int,
    @SerialName("is_a") // is this question active
    val isQuestionActive: Boolean,
    @SerialName("t") // type of available response
    val questionType: String,
    @SerialName("q") // question title
    val questionTitle: String,
    @SerialName("a") // array of answers
    val answersList: List<WsAnswer>
)

@Serializable
data class WsAnswer(
    @SerialName("id") // id of answer
    val answerId: Int,
    @SerialName("a") // title of answer
    val answerTitle: String
)