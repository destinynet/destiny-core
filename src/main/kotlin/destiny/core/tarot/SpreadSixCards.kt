/**
 * Created by smallufo on 2023-12-19.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@SerialName("6cards")
data class SpreadSixCards(val lt: CardOrientation, val mt: CardOrientation, val rt: CardOrientation,
                          val lb: CardOrientation, val mb: CardOrientation, val rb: CardOrientation) : ISpread {

  override fun getTitle(locale: Locale): String {
    return SpreadSixCards.getTitle(locale)
  }

  override fun getLocalePosMap(): List<Pair<CardOrientation, Map<Locale, String>>> {
    return listOf(
      lt to mapOf(
        Locale.TAIWAN to "左上"
      ),
      mt to mapOf(
        Locale.TAIWAN to "中上"
      ),
      rt to mapOf(
        Locale.TAIWAN to "右上"
      ),
      lb to mapOf(
        Locale.TAIWAN to "左下"
      ),
      mb to mapOf(
        Locale.TAIWAN to "中下"
      ),
      rb to mapOf(
        Locale.TAIWAN to "右下"
      )
    )
  }

  companion object : ILocaleString {
    override fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "Six Card Spread"
        "ja" -> "六枚カード"
        else -> "六牌法"
      }
    }
  }
}
