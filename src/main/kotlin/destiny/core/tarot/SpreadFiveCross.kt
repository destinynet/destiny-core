/**
 * Created by smallufo on 2023-11-25.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import destiny.tools.Lang

@Serializable
@SerialName("Cross5Spread")
data class SpreadFiveCross(
  val center: CardOrientation,
  val left: CardOrientation,
  val right: CardOrientation,
  val top: CardOrientation,
  val bottom: CardOrientation
) : ISpread {

  override fun getTitle(lang: Lang): String {
    return SpreadFiveCross.getTitle(lang)
  }

  override fun getLangPosMap(): List<Pair<CardOrientation, Map<Lang, String>>> {
    return listOf(
      center to mapOf(
        Lang.ZH_TW to "中央",
        Lang.JA to "中央",
        Lang.EN to "Middle",
      ),
      left to mapOf(
        Lang.ZH_TW to "左方",
        Lang.JA to "左",
        Lang.EN to "Left",
      ),
      right to mapOf(
        Lang.ZH_TW to "右方",
        Lang.JA to "右",
        Lang.EN to "Right",
      ),
      top to mapOf(
        Lang.ZH_TW to "上方",
        Lang.JA to "上",
        Lang.EN to "Top",
      ),
      bottom to mapOf(
        Lang.ZH_TW to "下方",
        Lang.JA to "下",
        Lang.EN to "Bottom",
      )
    )
  }

  companion object : ILocaleString {
    override fun getTitle(lang: Lang): String {
      return when (lang.language) {
        "en" -> "The Cross Spread"
        "ja" -> "十字のスプレッド"
        else -> "十字陣"
      }
    }
  }
}
