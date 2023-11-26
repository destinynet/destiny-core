/**
 * Created by smallufo on 2023-11-25.
 */
package destiny.core.tarot

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

  override val cards: List<CardOrientation>
    get() = listOf(center, left, right, top, bottom)

  override fun getTitle(locale: Locale): String {
    return SpreadFiveCross.getTitle(locale)
  }

  companion object {
    fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "The Cross Spread"
        "ja" -> "十字のスプレッド"
        else -> "十字陣"
      }
    }
  }
}
