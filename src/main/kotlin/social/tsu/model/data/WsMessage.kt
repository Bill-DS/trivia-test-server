package social.tsu.model.data


import kotlinx.serialization.Serializable

/**
 * Simple message sent by Websocket trivia gam
 * in response to game subscription, and maybe other stuff.
 */
@Serializable
data class WsMessage(
    val data: String
): WebsocketTrivia