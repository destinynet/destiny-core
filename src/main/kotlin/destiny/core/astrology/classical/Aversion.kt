/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Planet

/**
 * 不相見（希臘化的 apostrophē，英文 aversion），以星座計：兩個星座相隔 2、6、8、12 個位置（星座差 1、5、7、11），
 * 彼此看不到 —— 沒有任何托勒密相位能連起來。
 *
 * 只收古典本命實際會讀的兩種（[Basis]），不列全部兩兩配對：
 * - [Basis.RULER]：[planet] 與它所在星座的主人 [from] 不相見 —— 主人照應不到它
 * - [Basis.ASCENDANT]：命主星 [planet] 與上升（[from] ＝ 上升點）不相見 —— 代表本人的星看不到自己的第一宮
 *
 * [place]：[planet] 落在 [from] 所在星座起算的第幾個星座（2、6、8、12；[destiny.core.astrology.ZodiacSign.placeFrom]）。
 * 經由映點相連的星座（「看得見」「聽得見」的星座對）不在此處理
 */
data class Aversion(
  val planet: Planet,
  val from: AstroPoint,
  val place: Int,
  val basis: Basis,
) : ClassicalFact {

  enum class Basis { RULER, ASCENDANT }

  companion object {
    /** 起算的星座算第 1 個：相隔第 2、6、8、12 個星座即不相見 */
    val AVERSE_PLACES: Set<Int> = setOf(2, 6, 8, 12)
  }
}
