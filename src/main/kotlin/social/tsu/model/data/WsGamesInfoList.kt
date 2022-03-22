package social.tsu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.tsu.model.data.WebsocketTrivia


/**
 * Background message from websocket list upcoming or on going games.
 */
@Serializable
data class WsGamesInfoList(
    val type: String = "games_list",
    val data: List<GameInfo>
) : WebsocketTrivia

/**
 * Info describing an upcoming or ongoing trivia game.
 *
 */
@Serializable
data class GameInfo(

    /**
     * Game Id.
     *  Note: ws is returning a String int, Moshi decodes either.
     */
    val id: Int,

    val title: String,

    @SerialName("starts_at")
    val startTime: String,   // TODO created an adapter for ZonedDateTime

    @SerialName("survey_type")
    val surveyType: Int,

    @SerialName("game_status")
    val gameStatus: Int
)