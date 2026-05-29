/**
 * YearMonth Search 回測度量的純單元測試。
 */
package destiny.core.astrology.prediction

import org.junit.jupiter.api.Nested
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class YearMonthBacktestTest {

  private val tol = 1e-9

  /** 造一個單月、指定 strength 的 window(內容無關緊要)。 */
  private fun win(ym: YearMonth, strength: Double, to: YearMonth = ym) =
    YearMonthWindow(ym, to, strength, emptyList(), emptyList())

  // 已依 strength 降冪的 5 個 window(1月..5月)
  private val windows = listOf(
    win(YearMonth.of(2026, 1), 0.9),
    win(YearMonth.of(2026, 2), 0.8),
    win(YearMonth.of(2026, 3), 0.7),
    win(YearMonth.of(2026, 4), 0.6),
    win(YearMonth.of(2026, 5), 0.5),
  )

  @Nested
  inner class Evaluate {

    @Test
    fun rankOne_isBest() {
      val r = YearMonthBacktest.evaluate(windows, YearMonth.of(2026, 1))
      assertTrue(r.matched)
      assertEquals(1, r.rank)
      assertEquals(5, r.totalWindows)
      assertEquals(1.0, r.percentile!!, tol)   // (5 - 1 + 1)/5
      assertTrue(r.inTopN)
    }

    @Test
    fun rankThreeOfFive() {
      val r = YearMonthBacktest.evaluate(windows, YearMonth.of(2026, 3), topN = 3)
      assertEquals(3, r.rank)
      assertEquals(0.6, r.percentile!!, tol)    // (5 - 3 + 1)/5
      assertTrue(r.inTopN)
    }

    @Test
    fun belowTopN_notInTopN() {
      val r = YearMonthBacktest.evaluate(windows, YearMonth.of(2026, 4), topN = 3)
      assertEquals(4, r.rank)
      assertFalse(r.inTopN)
    }

    @Test
    fun noCoveringWindow_isUnmatched() {
      val r = YearMonthBacktest.evaluate(windows, YearMonth.of(2026, 12))
      assertFalse(r.matched)
      assertNull(r.rank)
      assertNull(r.percentile)
      assertFalse(r.inTopN)
      assertEquals(5, r.totalWindows)
    }

    @Test
    fun monthInsideMultiMonthWindow_matches() {
      val merged = listOf(win(YearMonth.of(2026, 3), 0.9, to = YearMonth.of(2026, 5)))
      val r = YearMonthBacktest.evaluate(merged, YearMonth.of(2026, 4))   // 落在 3..5 的 window 內
      assertTrue(r.matched)
      assertEquals(1, r.rank)
    }

    @Test
    fun emptyWindows_isUnmatched() {
      val r = YearMonthBacktest.evaluate(emptyList(), YearMonth.of(2026, 4))
      assertFalse(r.matched)
      assertEquals(0, r.totalWindows)
    }
  }

  @Nested
  inner class Summarize {

    @Test
    fun aggregatesHitRateAndMeanPercentile() {
      val results = listOf(
        YearMonthBacktest.evaluate(windows, YearMonth.of(2026, 1), topN = 3),  // rank1, pct 1.0, inTopN
        YearMonthBacktest.evaluate(windows, YearMonth.of(2026, 4), topN = 3),  // rank4, pct 0.4, not inTopN
        YearMonthBacktest.evaluate(windows, YearMonth.of(2026, 12), topN = 3), // unmatched
      )
      val s = YearMonthBacktest.summarize(results)
      assertEquals(2, s.matchedCount)
      assertEquals(1.0 / 3.0, s.topNHitRate, tol)         // 1 of 3 inTopN
      assertEquals((1.0 + 0.4) / 2.0, s.meanPercentile, tol) // mean over matched
    }

    @Test
    fun empty_isZeroes() {
      val s = YearMonthBacktest.summarize(emptyList())
      assertEquals(0, s.matchedCount)
      assertEquals(0.0, s.topNHitRate, tol)
      assertEquals(0.0, s.meanPercentile, tol)
    }
  }
}
