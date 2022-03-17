package social.tsu.android.network.model.trivia.websocket



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import social.tsu.model.data.WebsocketTrivia

/**
 * The websocket version of the final game results.
 */
@Serializable
data class WsEndGameResults(
    @SerialName("g_id") // game id
    val gameId: Int,
    @SerialName("game_type") // 'poll' or 'trivium'
    val gameType: String,
    @SerialName("game_status") // 0:stopped, 1:paused, 2:started, 3:completed
    val gameState: Int,
    @SerialName("prize_pool") // (Float) the total prize pool
    val prizePool: Float,
    @SerialName("num_winners") // (Int) the total winners for this game
    val numberOfWinners: Int,
    @SerialName("share") // (Float) the share of the prize pool won for the user
    val share: Float,
    @SerialName("percent_correct") // (Int) the percent of correct answers required for jackpot
    val jackpotPercentBarrier: Int?

) : WebsocketTrivia
