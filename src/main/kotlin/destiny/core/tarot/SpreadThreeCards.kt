/**
 * Created by smallufo on 2023-04-02.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import destiny.tools.Lang

/**
 * maybe Major Arcana
 */
@Serializable
@SerialName("3cards")
data class SpreadThreeCards(@SerialName("c1") val card1: CardOrientation,
                            @SerialName("c2") val card2: CardOrientation,
                            @SerialName("c3") val card3: CardOrientation) : ISpread {

  override fun getLangPosMap(): List<Pair<CardOrientation, Map<Lang, String>>> {
    return listOf(
      card1 to
        mapOf(
          Lang.ZH_TW to "左方",
          Lang.JA to "左",
          Lang.EN to "Left",
        ),
      card2 to mapOf(
        Lang.ZH_TW to "中央",
        Lang.JA to "中央",
        Lang.EN to "Middle",
      ),
      card3 to mapOf(
        Lang.ZH_TW to "右方",
        Lang.JA to "右方",
        Lang.EN to "Right",
      ),
    )
  }

  override fun getTitle(lang: Lang): String {
    return SpreadThreeCards.getTitle(lang)
  }

  companion object : ILocaleString {
    fun of(list: List<CardOrientation>): SpreadThreeCards {
      require(list.size == 3)
      return SpreadThreeCards(list[0], list[1], list[2])
    }

    override fun getTitle(lang: Lang): String {
      return when (lang.language) {
        "en" -> "Three Card Spread"
        "ja" -> "三枚カード"
        else -> "三牌法"
      }
    }
  }
}
