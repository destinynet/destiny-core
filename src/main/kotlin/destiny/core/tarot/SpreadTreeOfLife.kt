/**
 * Created by smallufo on 2025-03-22.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import destiny.tools.Lang

/**
 *           (1) Kether - Crown
 *              |
 *    (2) Chokmah - Wisdom    (3) Binah - Understanding
 *              |               |
 *    (4) Chesed - Mercy   --   (5) Geburah - Severity
 *              |               |
 *           (6) Tiphereth - Beauty
 *              |
 *    (7) Netzach - Victory   (8) Hod - Glory
 *              |               |
 *           (9) Yesod - Foundation
 *              |
 *          (10) Malkuth - Kingdom
 *
 */
@Serializable
@SerialName("Tree_of_Life")
data class SpreadTreeOfLife(
  val kether: CardOrientation,
  val chokmah: CardOrientation,
  val binah: CardOrientation,
  val chesed: CardOrientation,
  val geburah: CardOrientation,
  val tiphereth: CardOrientation,
  val netzach: CardOrientation,
  val hod: CardOrientation,
  val yesod: CardOrientation,
  val malkuth: CardOrientation
) : ISpread {

  override fun getTitle(lang: Lang): String {
    return SpreadTreeOfLife.getTitle(lang)
  }

  override fun getLangPosMap(): List<Pair<CardOrientation, Map<Lang, String>>> {
    return listOf(
      kether to mapOf(
        Lang.ZH_TW to "王冠 (Kether)",
        Lang.JA to "ケテル (Kether)",
        Lang.EN to "Crown (Kether)"
      ),
      chokmah to mapOf(
        Lang.ZH_TW to "智慧 (Chokmah)",
        Lang.JA to "コクマー (Chokmah)",
        Lang.EN to "Wisdom (Chokmah)"
      ),
      binah to mapOf(
        Lang.ZH_TW to "理解 (Binah)",
        Lang.JA to "ビナー (Binah)",
        Lang.EN to "Understanding (Binah)"
      ),
      chesed to mapOf(
        Lang.ZH_TW to "慈悲 (Chesed)",
        Lang.JA to "ケセド (Chesed)",
        Lang.EN to "Mercy (Chesed)"
      ),
      geburah to mapOf(
        Lang.ZH_TW to "嚴厲 (Geburah)",
        Lang.JA to "ゲブラー (Geburah)",
        Lang.EN to "Severity (Geburah)"
      ),
      tiphereth to mapOf(
        Lang.ZH_TW to "美 (Tiphereth)",
        Lang.JA to "ティファレト (Tiphereth)",
        Lang.EN to "Beauty (Tiphereth)"
      ),
      netzach to mapOf(
        Lang.ZH_TW to "勝利 (Netzach)",
        Lang.JA to "ネツァク (Netzach)",
        Lang.EN to "Victory (Netzach)"
      ),
      hod to mapOf(
        Lang.ZH_TW to "榮耀 (Hod)",
        Lang.JA to "ホド (Hod)",
        Lang.EN to "Glory (Hod)"
      ),
      yesod to mapOf(
        Lang.ZH_TW to "基礎 (Yesod)",
        Lang.JA to "イェソド (Yesod)",
        Lang.EN to "Foundation (Yesod)"
      ),
      malkuth to mapOf(
        Lang.ZH_TW to "王國 (Malkuth)",
        Lang.JA to "マルクト (Malkuth)",
        Lang.EN to "Kingdom (Malkuth)"
      )
    )
  }

  companion object : ILocaleString {
    override fun getTitle(lang: Lang): String {
      return when (lang.language) {
        "en" -> "Tree of Life Spread"
        "ja" -> "生命の樹スプレッド"
        else -> "生命之樹排陣"
      }
    }
  }
}
