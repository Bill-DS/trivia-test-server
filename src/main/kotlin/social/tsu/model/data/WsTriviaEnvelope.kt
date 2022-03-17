package social.tsu.model.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Marker interface
 */
interface WsTriviaResponse {
}

/**
 * Envelope for data going to the backend.
 */
@Serializable
data class WsTriviaEnvelope<T: WsTriviaResponse>(

    val action: String,

    val data: T
)


/**
 * Message sent in response to the game list message.
 */
@Serializable
data class WsTriviaGameSubscription(

    @SerialName("game_id")
    val gameId: Int

) : WsTriviaResponse {
    companion object {
        val action = "subscribe"
    }
}

/**
 * Message sent to close the connection.
 * Differentiated from above by the "action" field in the Envelope.
 */
@Serializable
data class WsTriviaGameUnsubscription(

    @SerialName("game_id")
    val gameId: Int

) : WsTriviaResponse {
    companion object {
        val action = "unsubscribe"
    }
}
