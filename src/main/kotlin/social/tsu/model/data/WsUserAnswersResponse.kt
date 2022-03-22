package social.tsu.model.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsUserAnswersResponse(

    @SerialName("user_answers")
    val answers: List<WsTriviaAnswerAcknowledgementWrapper>
): WebsocketTrivia

@Serializable
data class WsTriviaAnswerAcknowledgementWrapper(

    @SerialName("user_answer")
    val answer: WsTriviaAnswerAcknowledgement
)

@Serializable
data class WsTriviaAnswerAcknowledgement(

    @SerialName("question_id")
    val questionId: Int,

    @SerialName("answer_id")
    val answerId: Int,

    val accepted: Boolean,

    val error: Boolean,

    val msg: String? = null
)

fun createAnswerAcknowledgement( questionId: Int, answerId: Int, accepted: Boolean) =
    WsUserAnswersResponse(
        answers = listOf(
            WsTriviaAnswerAcknowledgementWrapper(
                WsTriviaAnswerAcknowledgement(
                    questionId = questionId,
                    answerId = answerId,
                    accepted = accepted,
                    error = false
                )
            )
        )
    )
