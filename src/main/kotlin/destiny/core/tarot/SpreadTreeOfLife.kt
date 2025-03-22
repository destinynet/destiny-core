/**
 * Created by smallufo on 2025-03-22.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import java.util.Locale.*

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

  override fun getTitle(locale: Locale): String {
    return SpreadTreeOfLife.getTitle(locale)
  }

  override fun getLocalePosMap(): List<Pair<CardOrientation, Map<Locale, String>>> {
    return listOf(
      kether to mapOf(
        TAIWAN to "王冠 (Kether)",
        JAPANESE to "ケテル (Kether)",
        ENGLISH to "Crown (Kether)"
      ),
      chokmah to mapOf(
        TAIWAN to "智慧 (Chokmah)",
        JAPANESE to "コクマー (Chokmah)",
        ENGLISH to "Wisdom (Chokmah)"
      ),
      binah to mapOf(
        TAIWAN to "理解 (Binah)",
        JAPANESE to "ビナー (Binah)",
        ENGLISH to "Understanding (Binah)"
      ),
      chesed to mapOf(
        TAIWAN to "慈悲 (Chesed)",
        JAPANESE to "ケセド (Chesed)",
        ENGLISH to "Mercy (Chesed)"
      ),
      geburah to mapOf(
        TAIWAN to "嚴厲 (Geburah)",
        JAPANESE to "ゲブラー (Geburah)",
        ENGLISH to "Severity (Geburah)"
      ),
      tiphereth to mapOf(
        TAIWAN to "美 (Tiphereth)",
        JAPANESE to "ティファレト (Tiphereth)",
        ENGLISH to "Beauty (Tiphereth)"
      ),
      netzach to mapOf(
        TAIWAN to "勝利 (Netzach)",
        JAPANESE to "ネツァク (Netzach)",
        ENGLISH to "Victory (Netzach)"
      ),
      hod to mapOf(
        TAIWAN to "榮耀 (Hod)",
        JAPANESE to "ホド (Hod)",
        ENGLISH to "Glory (Hod)"
      ),
      yesod to mapOf(
        TAIWAN to "基礎 (Yesod)",
        JAPANESE to "イェソド (Yesod)",
        ENGLISH to "Foundation (Yesod)"
      ),
      malkuth to mapOf(
        TAIWAN to "王國 (Malkuth)",
        JAPANESE to "マルクト (Malkuth)",
        ENGLISH to "Kingdom (Malkuth)"
      )
    )
  }

  companion object : ILocaleString {
    override fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "Tree of Life Spread"
        "ja" -> "生命の樹スプレッド"
        else -> "生命之樹排陣"
      }
    }
  }
}
