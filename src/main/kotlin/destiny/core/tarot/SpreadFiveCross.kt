/**
 * Created by smallufo on 2023-11-25.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

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
        Locale.TAIWAN to "中間"
      ),
      left to mapOf(
        Locale.TAIWAN to "左方"
      ),
      right to mapOf(
        Locale.TAIWAN to "右方"
      ),
      top to mapOf(
        Locale.TAIWAN to "上方"
      ),
      bottom to mapOf(
        Locale.TAIWAN to "下方"
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
