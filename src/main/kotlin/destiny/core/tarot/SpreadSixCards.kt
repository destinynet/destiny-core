package destiny.core.tarot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by smallufo on 2023-12-19.
 */
@Serializable
@SerialName("6cards")
data class SpreadSixCards(val lt: CardOrientation, val mt: CardOrientation, val rt: CardOrientation,
                          val lb: CardOrientation, val mb: CardOrientation, val rb: CardOrientation) : ISpread {
  override val cards: List<CardOrientation> = listOf(lt, mt, rt, lb, mb, rb)
  override fun getTitle(locale: Locale): String {
    return SpreadSixCards.getTitle(locale)
  }

  companion object {
    fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "Six Card Spread"
        "ja" -> "六枚カード"
        else -> "六牌法"
      }
    }
  }
}
