/**
 * Created by smallufo on 2023-12-24.
 */
package destiny.core.tarot

import destiny.core.astrology.Element
import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import destiny.tools.Lang


@Serializable
@SerialName("4SeasonSpread")
data class SpreadSeason(
  val center: CardOrientation,
  val map: Map<Element, CardOrientation>
) : ISpread {

  override fun getTitle(lang: Lang): String {
    return SpreadSeason.getTitle(lang)
  }

  override fun getLangPosMap(): List<Pair<CardOrientation, Map<Lang, String>>> {
    return buildList {
      add(
        center to mapOf(
          Lang.ZH_TW to "中央",
          Lang.JA to "中央",
          Lang.EN to "Middle",
        )
      )

      add(
        map.getValue(Element.FIRE) to mapOf(
          Lang.ZH_TW to "左方",
          Lang.JA to "左",
          Lang.EN to "Left",
        )
      )

      add(
        map.getValue(Element.WATER) to mapOf(
          Lang.ZH_TW to "下方",
          Lang.JA to "下",
          Lang.EN to "Bottom",
        )
      )

      add(
        map.getValue(Element.AIR) to mapOf(
          Lang.ZH_TW to "右方",
          Lang.JA to "右方",
          Lang.EN to "Right",
        )
      )

      add(
        map.getValue(Element.EARTH) to mapOf(
          Lang.ZH_TW to "上方",
          Lang.JA to "上",
          Lang.EN to "Top",
        )
      )
    }
  }

  companion object : ILocaleString {
    override fun getTitle(lang: Lang): String {
      return when (lang.language) {
        "en" -> "Seasonal Tarot"
        "ja" -> "四季タロット"
        else -> "四季牌陣"
      }
    }

  }
}
