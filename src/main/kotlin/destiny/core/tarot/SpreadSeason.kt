/**
 * Created by smallufo on 2023-12-24.
 */
package destiny.core.tarot

import destiny.core.astrology.Element
import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import java.util.Locale.*


@Serializable
@SerialName("4SeasonSpread")
data class SpreadSeason(
  val center: CardOrientation,
  val map: Map<Element, CardOrientation>
) : ISpread {

  override fun getTitle(locale: Locale): String {
    return SpreadSeason.getTitle(locale)
  }

  override fun getLocalePosMap(): List<Pair<CardOrientation, Map<Locale, String>>> {
    return buildList {
      add(
        center to mapOf(
          TAIWAN to "中央",
          JAPANESE to "中央",
          ENGLISH to "Middle",
        )
      )

      add(
        map[Element.FIRE]!! to mapOf(
          TAIWAN to "左方",
          JAPANESE to "左",
          ENGLISH to "Left",
        )
      )

      add(
        map[Element.WATER]!! to mapOf(
          TAIWAN to "下方",
          JAPANESE to "下",
          ENGLISH to "Bottom",
        )
      )

      add(
        map[Element.AIR]!! to mapOf(
          TAIWAN to "右方",
          JAPANESE to "右方",
          ENGLISH to "Right",
        )
      )

      add(
        map[Element.EARTH]!! to mapOf(
          TAIWAN to "上方",
          JAPANESE to "上",
          ENGLISH to "Top",
        )
      )
    }
  }

  companion object : ILocaleString {
    override fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "Seasonal Tarot"
        "ja" -> "四季タロット"
        else -> "四季牌陣"
      }
    }

  }
}
