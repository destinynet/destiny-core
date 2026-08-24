/**
 * LensCalibrator 的純單元測試(假資料)。Yearly Peaks B2,
 * root docs/plans/2026-08-24-yearly-peaks-algorithm.md §3.4。
 */
package destiny.core.astrology.prediction

import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LensCalibrationTest {

  private fun result(rank: Int?, total: Int = 12, topN: Int = 3): BacktestResult {
    val matched = rank != null
    return BacktestResult(
      actualMonth = YearMonth.of(2020, 1),
      matched = matched,
      rank = rank,
      totalWindows = total,
      percentile = rank?.let { (total - it + 1).toDouble() / total },
      inTopN = rank != null && rank <= topN,
      topN = topN,
    )
  }

  @Test
  fun emptyResults_isUntested() {
    val c = LensCalibrator.calibrate("x", emptyList())
    assertEquals(CalibrationTier.UNTESTED, c.tier)
    assertEquals(0, c.sampleCount)
    assertEquals(0.0, c.topNHitRate)
  }

  @Test
  fun belowMinSamples_isUntested_evenWithPerfectHits() {
    // 2 筆全中仍 UNTESTED —— 樣本不足不做宣稱
    val c = LensCalibrator.calibrate("x", listOf(result(1), result(2)))
    assertEquals(CalibrationTier.UNTESTED, c.tier)
    assertEquals(1.0, c.topNHitRate)
  }

  @Test
  fun atMinSamples_hitRateAtThreshold_isStrong() {
    // 4 筆中 2 筆入 top-3 → 0.5 = 門檻 → STRONG(≥)
    val c = LensCalibrator.calibrate("x", listOf(result(1), result(3), result(7), result(null)))
    assertEquals(CalibrationTier.TESTED_STRONG, c.tier)
    assertEquals(0.5, c.topNHitRate)
  }

  @Test
  fun atMinSamples_hitRateBelowThreshold_isWeak() {
    // 3 筆中 1 筆入 top-3 → 0.33 → WEAK
    val c = LensCalibrator.calibrate("x", listOf(result(2), result(8), result(null)))
    assertEquals(CalibrationTier.TESTED_WEAK, c.tier)
  }

  @Test
  fun unmatchedResults_countAgainstHitRate_notDropped() {
    // 未被任何窗覆蓋的事件是反證,必須留在分母
    val c = LensCalibrator.calibrate("x", listOf(result(1), result(null), result(null), result(null)))
    assertEquals(0.25, c.topNHitRate)
    assertEquals(4, c.sampleCount)
    assertEquals(CalibrationTier.TESTED_WEAK, c.tier)
  }
}
