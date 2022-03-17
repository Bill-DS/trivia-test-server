package social.tsu.model.data

data class SampleQuestion(
    val question: String,
    val answers: List<String>,
    val correct: Int
)

fun sq( q: String, c: Int, vararg a: String): SampleQuestion =
    SampleQuestion(
        question = q,
        answers = a.toList(),
        correct = c.coerceIn(a.indices)
    )
