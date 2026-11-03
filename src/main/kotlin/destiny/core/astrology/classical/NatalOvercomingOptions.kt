/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.Planet

/**
 * 本命盤真正讀的居上（[IOvercoming] 的篩選）：不列全部兩兩配對（幾乎每對星都有星座關係），只收下列幾種。
 *
 * - [malefics] 以 [maleficAspects] 居上：預設只收四分（decimation，居上者落在被壓者起算的第十個星座）—— 凶星以三分、六分居上，古典視為緩和
 * - [benefics] 以 [beneficAspects] 居上：預設六分、四分、三分都收；四分是最強的提攜
 * - 被居上的：[lights] ＝ 日、月；[ascendantRuler] ＝ 命主星。福點等非行星的點不在此列
 *
 * 吉凶星依日夜盤而變的細節（得時的凶星較輕）不在這裡篩，交給判讀。
 * 受剋／受扶的「被居上」條件（[MaltreatmentOptions.maleficOvercoming]／[MaltreatmentOptions.beneficOvercoming]）預設取同一組相位
 */
data class NatalOvercomingOptions(
  val malefics: Set<Planet> = IBesiegement.MALEFICS,
  val benefics: Set<Planet> = IBesiegement.BENEFICS,
  val maleficAspects: Set<Aspect> = MALEFIC_ASPECTS,
  val beneficAspects: Set<Aspect> = BENEFIC_ASPECTS,
  val lights: Boolean = true,
  val ascendantRuler: Boolean = true,
) {

  /** 居上者 → 收哪些居上相位；凶星在前、吉星在後（輸出順序依此） */
  val superiors: List<Pair<Planet, Set<Aspect>>>
    get() = malefics.map { it to maleficAspects } + benefics.map { it to beneficAspects }

  companion object {
    /** 凶星的居上：只收四分 */
    val MALEFIC_ASPECTS: Set<Aspect> = setOf(Aspect.SQUARE)

    /** 吉星的居上：六分、四分、三分 */
    val BENEFIC_ASPECTS: Set<Aspect> = setOf(Aspect.SEXTILE, Aspect.SQUARE, Aspect.TRINE)

    /** 希臘化本命的通行讀法（凶星四分、吉星六分／四分／三分；日、月、命主星） */
    val DEFAULT = NatalOvercomingOptions()
  }
}
