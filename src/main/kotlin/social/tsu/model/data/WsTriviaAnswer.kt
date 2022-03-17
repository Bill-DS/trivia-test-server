package social.tsu.model.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class WsTriviaAnswer(

    @SerialName("question_id")
    val questionId: Int,

    @SerialName("answer_id")
    val answerId: Int
)

/**
 * Extra answer object wrapper from the department of redundancy department.
 */
@Serializable
data class WsTriviaAnswerWrapper(

    @SerialName("user_answer")
    val answer: WsTriviaAnswer
)

/**
 * Penultimate wrapper around the trivia answer. Still goes inside
 * an envelope.
 */
@Serializable
data class WsTriviaAnswerResponse(

    @SerialName("game_id")
    val gameId: Int,

    @SerialName("user_answers")
    val answers: List<WsTriviaAnswerWrapper>
) : WsTriviaResponse

