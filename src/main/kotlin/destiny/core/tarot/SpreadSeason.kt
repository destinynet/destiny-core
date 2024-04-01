/**
 * Created by smallufo on 2023-12-24.
 */
package destiny.core.tarot

import destiny.core.astrology.Element
import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*


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
          Locale.TAIWAN to "中間"
        )
      )

      add(
        map[Element.FIRE]!! to mapOf(
          Locale.TAIWAN to "左方"
        )
      )

      add(
        map[Element.WATER]!! to mapOf(
          Locale.TAIWAN to "下方"
        )
      )

      add(
        map[Element.AIR]!! to mapOf(
          Locale.TAIWAN to "右方"
        )
      )

      add(
        map[Element.EARTH]!! to mapOf(
          Locale.TAIWAN to "上方"
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
