/**
 * Created by smallufo on 2023-12-19.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import destiny.tools.Lang

@Serializable
@SerialName("6cards")
data class SpreadSixCards(val lt: CardOrientation, val mt: CardOrientation, val rt: CardOrientation,
                          val lb: CardOrientation, val mb: CardOrientation, val rb: CardOrientation) : ISpread {

  override fun getTitle(lang: Lang): String {
    return SpreadSixCards.getTitle(lang)
  }

  override fun getLangPosMap(): List<Pair<CardOrientation, Map<Lang, String>>> {
    return listOf(
      lt to mapOf(
        Lang.ZH_TW to "左上",
        Lang.JA to "左上",
        Lang.EN to "Upper left"
      ),
      mt to mapOf(
        Lang.ZH_TW to "中上",
        Lang.JA to "中上",
        Lang.EN to "Middle top",
      ),
      rt to mapOf(
        Lang.ZH_TW to "右上",
        Lang.JA to "右上",
        Lang.EN to "Top right",
      ),
      lb to mapOf(
        Lang.ZH_TW to "左下",
        Lang.JA to "左下",
        Lang.EN to "Left bottom",
      ),
      mb to mapOf(
        Lang.ZH_TW to "中下",
        Lang.JA to "中下",
        Lang.EN to "Middle bottom",
      ),
      rb to mapOf(
        Lang.ZH_TW to "右下",
        Lang.JA to "右下",
        Lang.EN to "Right bottom",
      )
    )
  }

  companion object : ILocaleString {
    override fun getTitle(lang: Lang): String {
      return when (lang.language) {
        "en" -> "Six Card Spread"
        "ja" -> "六枚カード"
        else -> "六牌法"
      }
    }
  }
}
