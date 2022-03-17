package social.tsu.model.repo

import kotlinx.serialization.json.Json
import social.tsu.model.data.WsTriviaAnswerResponse
import social.tsu.model.data.WsTriviaEnvelope
import social.tsu.model.data.WsTriviaGameSubscription
import social.tsu.model.data.WsTriviaGameUnsubscription

class TriviaSerializer {

    fun decodeString(text: String): Any? {

        tryOrNull {
            Json.decodeFromString( WsTriviaEnvelope.serializer(WsTriviaGameSubscription.serializer()), text)
        }?.let {
            return when (it.action) {
                WsTriviaGameSubscription.action -> it.data
                WsTriviaGameUnsubscription.action -> WsTriviaGameUnsubscription(it.data.gameId)
                else -> null
            }
        }

        tryOrNull {
            Json.decodeFromString( WsTriviaEnvelope.serializer(WsTriviaAnswerResponse.serializer()), text)
        }?.let { return it.data }

        println("--- unrecognized message: $text")
        return null
    }
}

fun <T> tryOrNull( block: ()->T): T? =
    try {
        block()
    } catch (t: Throwable){
        null
    }
