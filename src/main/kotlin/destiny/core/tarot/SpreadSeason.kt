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
  override val cards: List<CardOrientation>
    get() {
      return buildList {
        add(center)
        addAll(map.map { entry -> entry.value })
      }
    }

  override fun getTitle(locale: Locale): String {
    return SpreadSeason.getTitle(locale)
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
