/**
 * Created by smallufo on 2023-11-25.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import java.util.Locale.*

@Serializable
@SerialName("Cross5Spread")
data class SpreadFiveCross(
  val center: CardOrientation,
  val left: CardOrientation,
  val right: CardOrientation,
  val top: CardOrientation,
  val bottom: CardOrientation
) : ISpread {

  override fun getTitle(locale: Locale): String {
    return SpreadFiveCross.getTitle(locale)
  }

  override fun getLocalePosMap(): List<Pair<CardOrientation, Map<Locale, String>>> {
    return listOf(
      center to mapOf(
        TAIWAN to "中央",
        JAPANESE to "中央",
        ENGLISH to "Middle",
      ),
      left to mapOf(
        TAIWAN to "左方",
        JAPANESE to "左",
        ENGLISH to "Left",
      ),
      right to mapOf(
        TAIWAN to "右方",
        JAPANESE to "右",
        ENGLISH to "Right",
      ),
      top to mapOf(
        TAIWAN to "上方",
        JAPANESE to "上",
        ENGLISH to "Top",
      ),
      bottom to mapOf(
        TAIWAN to "下方",
        JAPANESE to "下",
        ENGLISH to "Bottom",
      )
    )
  }

  companion object : ILocaleString {
    override fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "The Cross Spread"
        "ja" -> "十字のスプレッド"
        else -> "十字陣"
      }
    }
  }
}
