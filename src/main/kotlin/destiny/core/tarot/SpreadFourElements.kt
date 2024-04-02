/**
 * Created by smallufo on 2023-04-05.
 */
package destiny.core.tarot

import destiny.core.astrology.Element
import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import java.util.Locale.*


@Serializable
@SerialName("4ElementsSpread")
data class SpreadFourElements(
  val fire: CardOrientation,
  val water: CardOrientation,
  val air: CardOrientation,
  val earth: CardOrientation
) : ISpread {

  override fun getTitle(locale: Locale): String {
    return SpreadFourElements.getTitle(locale)
  }

  fun getCardOrientation(element: Element): CardOrientation {
    return when (element) {
      Element.FIRE  -> fire
      Element.WATER -> water
      Element.AIR   -> air
      Element.EARTH -> earth
    }
  }

  override fun getLocalePosMap(): List<Pair<CardOrientation, Map<Locale, String>>> {
    return listOf(
      fire to mapOf(
        TAIWAN to "左上",
        JAPANESE to "左上",
        ENGLISH to "Left top",
      ),
      water to mapOf(
        TAIWAN to "右上",
        JAPANESE to "右上",
        ENGLISH to "Right top",
      ),
      earth to mapOf(
        TAIWAN to "左下",
        JAPANESE to "左下",
        ENGLISH to "Left bottom",
      ),
      air to mapOf(
        TAIWAN to "右下",
        JAPANESE to "右下",
        ENGLISH to "Right bottom",
      )
    )
  }

  companion object : ILocaleString {
    fun of(map: Map<Element, CardOrientation>): SpreadFourElements {
      require(map.size == 4)
      return SpreadFourElements(map[Element.FIRE]!!, map[Element.WATER]!!, map[Element.AIR]!!, map[Element.EARTH]!!)
    }

    override fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "The Four Elements Spread"
        "ja" -> "四元素スプレッド"
        else -> "四要素"
      }
    }
  }
}
