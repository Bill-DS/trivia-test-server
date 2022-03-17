package social.tsu.android.network.model.trivia.websocket



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.tsu.model.SurveyState
import social.tsu.model.data.WebsocketTrivia

/**
 * SurveyResult object as delivered from the Websocket stream
 */
@Serializable
data class WsSurveyResults(

    @SerialName("id") // the id of the poll / trivia
    val surveyId: Int = 1,

    @SerialName("g_id") // game id
    val gameId: Int,

    @SerialName("s") // survey state - 0:stopped, 1:paused, 2:started, 3:completed
    val surveyState: Int = SurveyState.STARTED.ordinal,

    @SerialName("t") // type of item(should be "results")
    val itemType: String = "Results",

    @SerialName("q") // array/list of questions
    val questionsList: List<WsQuestionResults>
) : WebsocketTrivia

@Serializable
data class WsQuestionResults(
    @SerialName("id") // id of question
    val questionId: Int,

    @SerialName("q_c") // question title
    val questionTitle: String,

    @SerialName("is_a") // is this question active
    val isQuestionActive: Boolean = true,

    @SerialName("nth")
    val questionOrder: Int,

    @SerialName("a") // array of answers
    val answersList: List<WsAnswerResults>
)

@Serializable
data class WsAnswerResults(

    @SerialName("id") // id of answer
    val answerId: Int,

    @SerialName("a_c") // title of answer
    val answerTitle: String,

    @SerialName("t") // total users who selected this answer
    val answersCount: Int?=null,

    @SerialName("p") // percent of users who selected this answer
    val answerPercentage: String?=null,

    @SerialName("c") //  represent correct answer
    val isAnswerCorrect: Boolean
)