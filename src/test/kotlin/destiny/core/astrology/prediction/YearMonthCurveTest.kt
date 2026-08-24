/**
 * Yearly Peaks B0 的純單元測試(無 Spring / 無 ephemeris):
 *  - 曲線曝露一致性([YearMonthScorer.buildCurve] vs [YearMonthScorer.buildWindows])
 *  - 谷抽取不變式([YearMonthTroughs])
 *
 * 對應 root docs/plans/2026-08-24-yearly-peaks-algorithm.md §六 的測試 #1、#2。
 * 依該文教訓,斷言只釘**結構性質**(一致性、不變式),不釘資料值。
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.Planet
import destiny.tools.Score.Companion.toScore
import org.junit.jupiter.api.Nested
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class YearMonthCurveTest {

  private val scorer = YearMonthScorer()
  private val tol = 1e-9
  private val noPeriods: (YearMonth) -> List<PeriodHit> = { emptyList() }
  private val venusTarget = HitTarget.Significator(Planet.VENUS)

  private fun instant(rawStrength: Double) =
    InstantHit.AstroPointHit(EventSource.TRANSIT, venusTarget, Planet.JUPITER, AspectContact(Aspect.TRINE, 1.0, true), rawStrength.toScore())

  private fun timed(ym: YearMonth, rawStrength: Double) = TimedInstantHit(ym, instant(rawStrength))

  private fun ym(month: Int) = YearMonth.of(2026, month)

  @Nested
  inner class BuildCurve {

    @Test
    fun empty_isEmpty() {
      val c = scorer.buildCurve(emptyList(), SearchGrain.MONTH, Combine.OR, noPeriods)
      assertTrue(c.strengths.isEmpty())
      assertTrue(c.windows.isEmpty())
    }

    @Test
    fun windows_identicalToBuildWindows() {
      // 含相鄰 run、谷值、與孤立月的混合形狀
      val timed = listOf(
        timed(ym(1), 0.6), timed(ym(2), 0.2), timed(ym(3), 0.7),  // run + 谷值切峰
        timed(ym(6), 0.4),                                        // 孤立月
        timed(ym(9), 0.5), timed(ym(10), 0.5),                    // 平頂 run
      )
      val windows = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods)
      val curve = scorer.buildCurve(timed, SearchGrain.MONTH, Combine.OR, noPeriods)
      assertEquals(windows, curve.windows, "buildWindows 必須是 buildCurve 的 .windows 投影")
    }

    @Test
    fun curveKeys_areExactlyHitMonths_preMerge() {
      val timed = listOf(timed(ym(1), 0.5), timed(ym(2), 0.5), timed(ym(4), 0.5))
      val c = scorer.buildCurve(timed, SearchGrain.MONTH, Combine.OR, noPeriods)
      // 曲線是合併前的逐月桶:1、2 月各自成鍵(即使 window 會把它們併成一窗)
      assertEquals(setOf(ym(1), ym(2), ym(4)), c.strengths.keys)
      // 合併後:1..2 月一窗 + 4 月一窗
      assertEquals(2, c.windows.size)
    }

    @Test
    fun windowStrength_equalsMaxOfCurveInItsRange() {
      val timed = listOf(
        timed(ym(1), 0.6), timed(ym(2), 0.2), timed(ym(3), 0.7),
        timed(ym(6), 0.4),
      )
      val c = scorer.buildCurve(timed, SearchGrain.MONTH, Combine.OR, noPeriods)
      assertTrue(c.windows.isNotEmpty())
      c.windows.forEach { w ->
        val inRange = c.strengths.filterKeys { it in w.from..w.to }.values
        assertTrue(inRange.isNotEmpty(), "window ${w.from}..${w.to} 必須涵蓋至少一個曲線鍵")
        assertEquals(inRange.max(), w.strength, tol, "window strength 必須等於範圍內曲線峰值")
      }
    }

    @Test
    fun flatRun_mergesToSingleWindow_notTwelveWeakPeaks() {
      // 全平的一年:12 個月同強度 → 一個高原窗,不是 12 個弱峰
      val timed = (1..12).map { timed(ym(it), 0.5) }
      val c = scorer.buildCurve(timed, SearchGrain.MONTH, Combine.OR, noPeriods)
      assertEquals(12, c.strengths.size)
      assertEquals(1, c.windows.size, "平頂 run 必須併成單一窗:${c.windows.map { "${it.from}..${it.to}" }}")
      assertEquals(ym(1), c.windows.single().from)
      assertEquals(ym(12), c.windows.single().to)
    }

    @Test
    fun yearGrain_curveKeyIsJanuary() {
      val timed = listOf(timed(YearMonth.of(2026, 5), 0.5), timed(YearMonth.of(2027, 8), 0.4))
      val c = scorer.buildCurve(timed, SearchGrain.YEAR, Combine.OR, noPeriods)
      assertEquals(setOf(YearMonth.of(2026, 1), YearMonth.of(2027, 1)), c.strengths.keys)
    }

    @Test
    fun curve_reflectsPeriodMultiplier() {
      val periods: (YearMonth) -> List<PeriodHit> = { m ->
        if (m == ym(3)) listOf(PeriodHit(PeriodSource.PROFECTION, "x", 2.0)) else emptyList()
      }
      val timed = listOf(timed(ym(3), 0.5), timed(ym(7), 0.5))
      val c = scorer.buildCurve(timed, SearchGrain.MONTH, Combine.OR, periods)
      assertEquals(1.0, c.strengths.getValue(ym(3)), tol)   // 0.5 × 2.0
      assertEquals(0.5, c.strengths.getValue(ym(7)), tol)
    }
  }

  @Nested
  inner class Troughs {

    private val jan = ym(1)
    private val dec = ym(12)

    private fun curveOf(vararg pairs: Pair<Int, Double>): Map<YearMonth, Double> =
      pairs.associate { (m, v) -> ym(m) to v }

    @Test
    fun flatNonZeroCurve_yieldsNoTroughs() {
      val flat = (1..12).map { it to 0.5 }.toTypedArray()
      assertTrue(YearMonthTroughs.extract(listOf(curveOf(*flat)), jan, dec).isEmpty())
    }

    @Test
    fun allZeroCurve_yieldsNoTroughs() {
      // 全 0 = 無命中 = 無相對結構:不宣稱「全年安靜」(那是沉默截斷,不是量測)
      assertTrue(YearMonthTroughs.extract(listOf(emptyMap()), jan, dec).isEmpty())
    }

    @Test
    fun zeroGapsBetweenBumps_becomeTroughs() {
      val curve = curveOf(3 to 0.8, 9 to 0.8)   // 其餘月缺鍵 = 0
      val troughs = YearMonthTroughs.extract(listOf(curve), jan, dec)
      assertEquals(
        listOf(TroughSpan(ym(1), ym(2)), TroughSpan(ym(4), ym(8)), TroughSpan(ym(10), ym(12))),
        troughs,
      )
    }

    @Test
    fun scalingInvariance() {
      val curve = curveOf(1 to 0.1, 3 to 0.8, 4 to 0.3, 7 to 0.05, 11 to 0.9)
      val scaled = curve.mapValues { it.value * 3.7 }
      assertEquals(
        YearMonthTroughs.extract(listOf(curve), jan, dec),
        YearMonthTroughs.extract(listOf(scaled), jan, dec),
        "曲線乘上任意正常數,峰谷結構必須不變",
      )
    }

    @Test
    fun multiLens_quietRequiresIntersection() {
      // A 上半年安靜、B 下半年安靜 → 無共同安靜月 → 無谷
      val lensA = curveOf(7 to 0.8, 8 to 0.8, 9 to 0.8, 10 to 0.8, 11 to 0.8, 12 to 0.8)
      val lensB = curveOf(1 to 0.8, 2 to 0.8, 3 to 0.8, 4 to 0.8, 5 to 0.8, 6 to 0.8)
      assertTrue(YearMonthTroughs.extract(listOf(lensA, lensB), jan, dec).isEmpty())

      // 兩者只在 4 月都安靜(高原月在門檻之上,不得被誤標)→ 谷 = [4 月]
      val lensC = curveOf(1 to 0.8, 2 to 0.8, 3 to 0.8, 5 to 0.8, 6 to 0.8, 7 to 0.8, 8 to 0.8, 9 to 0.8, 10 to 0.8, 11 to 0.8, 12 to 0.8)
      val lensD = curveOf(1 to 0.8, 2 to 0.8, 3 to 0.8, 5 to 0.8, 6 to 0.8, 7 to 0.8, 8 to 0.8, 9 to 0.8, 10 to 0.8, 11 to 0.8, 12 to 0.8)
      assertEquals(
        listOf(TroughSpan(ym(4), ym(4))),
        YearMonthTroughs.extract(listOf(lensC, lensD), jan, dec),
      )
    }

    @Test
    fun flatLens_isIgnored_doesNotVeto() {
      val structured = curveOf(3 to 0.8, 9 to 0.8)
      val flat = (1..12).associate { ym(it) to 0.5 }
      assertEquals(
        YearMonthTroughs.extract(listOf(structured), jan, dec),
        YearMonthTroughs.extract(listOf(structured, flat), jan, dec),
        "全平 lens 不得否決有分化 lens 的谷",
      )
    }

    @Test
    fun rangeBoundaries_areRespected() {
      // 只評 3..9 月:範圍外的鍵不參與
      val curve = curveOf(1 to 0.9, 5 to 0.8, 12 to 0.9)
      val troughs = YearMonthTroughs.extract(listOf(curve), ym(3), ym(9))
      assertTrue(troughs.all { !it.from.isBefore(ym(3)) && !it.to.isAfter(ym(9)) })
    }
  }
}
