/**
 * Created by smallufo on 2023-04-05.
 */
package destiny.core.tarot

import destiny.core.astrology.Element
import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
@SerialName("4ElementsSpread")
data class SpreadFourElements(
  val fire: CardOrientation,
  val water: CardOrientation,
  val air: CardOrientation,
  val earth: CardOrientation
) : ISpread {

  override val cards: List<CardOrientation>
    get() = listOf(fire, water, air, earth)

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
        Locale.TAIWAN to "左上"
      ),
      water to mapOf(
        Locale.TAIWAN to "右上"
      ),
      earth to mapOf(
        Locale.TAIWAN to "左下"
      ),
      air to mapOf(
        Locale.TAIWAN to "右下"
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
