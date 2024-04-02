/**
 * Created by smallufo on 2023-12-19.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import java.util.Locale.*

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
        TAIWAN to "左上",
        JAPANESE to "左上",
        ENGLISH to "Upper left"
      ),
      mt to mapOf(
        TAIWAN to "中上",
        JAPANESE to "中上",
        ENGLISH to "Middle top",
      ),
      rt to mapOf(
        TAIWAN to "右上",
        JAPANESE to "右上",
        ENGLISH to "Top right",
      ),
      lb to mapOf(
        TAIWAN to "左下",
        JAPANESE to "左下",
        ENGLISH to "Left bottom",
      ),
      mb to mapOf(
        TAIWAN to "中下",
        JAPANESE to "中下",
        ENGLISH to "Middle bottom",
      ),
      rb to mapOf(
        TAIWAN to "右下",
        JAPANESE to "右下",
        ENGLISH to "Right bottom",
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
