package social.tsu.model.data

import kotlinx.serialization.Serializable
import social.tsu.model.data.WebsocketTrivia

/**
 * Response sent by the BE when the websocket is first connected.
 */
@Serializable
data class WsTriviaConnected(

    val id: String,

    val status: String,

    val channels: List<String>
) : WebsocketTrivia