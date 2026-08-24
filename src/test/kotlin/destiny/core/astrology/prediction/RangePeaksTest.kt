/**
 * RangePeaks.assemble 的純單元測試(假資料,無 ephemeris)。
 * Yearly Peaks B1,root docs/plans/2026-08-24-yearly-peaks-algorithm.md §六 #6(純函式部分)。
 */
package destiny.core.astrology.prediction

import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RangePeaksTest {

  private fun ym(month: Int) = YearMonth.of(2026, month)
  private val jan = ym(1)
  private val dec = ym(12)

  private fun win(from: YearMonth, to: YearMonth, strength: Double) =
    YearMonthWindow(from, to, strength, emptyList(), emptyList())

  private fun lens(id: String, curve: Map<YearMonth, Double>, windows: List<YearMonthWindow>) =
    LensCurve(id, YearMonthCurve(curve, windows))

  @Test
  fun empty_isEmpty() {
    val m = RangePeaks.assemble(emptyList(), jan, dec)
    assertTrue(m.peaks.isEmpty())
    assertTrue(m.troughs.isEmpty())
  }

  @Test
  fun overlappingWindowsAcrossLenses_mergeIntoConfluentPeak() {
    val a = lens("a", mapOf(ym(3) to 0.8, ym(4) to 0.6), listOf(win(ym(3), ym(4), 0.8)))
    val b = lens("b", mapOf(ym(4) to 0.5, ym(5) to 0.5), listOf(win(ym(4), ym(5), 0.5)))
    val m = RangePeaks.assemble(listOf(a, b), jan, dec)

    assertEquals(1, m.peaks.size)
    val p = m.peaks.single()
    assertEquals(ym(3), p.from)                       // 範圍聯集
    assertEquals(ym(5), p.to)
    assertEquals(0.8, p.strength)                     // 取最大
    assertTrue(p.confluent)                           // 兩 lens 共振
    assertEquals(setOf("a", "b"), p.contributions.keys)
  }

  @Test
  fun adjacentButNotOverlapping_staySeparatePeaks() {
    // 谷值切峰切出來的相鄰窗是兩座峰,不得黏回去
    val a = lens("a", mapOf(ym(2) to 0.8, ym(3) to 0.8, ym(4) to 0.7, ym(5) to 0.7),
                 listOf(win(ym(2), ym(3), 0.8), win(ym(4), ym(5), 0.7)))
    val m = RangePeaks.assemble(listOf(a), jan, dec)
    assertEquals(2, m.peaks.size)
    assertTrue(m.peaks.none { it.confluent })
  }

  @Test
  fun topK_isRespected_strongestKept() {
    val windows = (1..8).map { win(ym(it), ym(it), it / 10.0) }   // 8 個不相鄰單月窗,strength 0.1..0.8
    val a = lens("a", (1..8).associate { ym(it) to it / 10.0 }, windows)
    val m = RangePeaks.assemble(listOf(a), jan, dec, topK = 3)
    assertEquals(3, m.peaks.size)
    assertEquals(listOf(0.8, 0.7, 0.6), m.peaks.map { it.strength })
  }

  @Test
  fun contributions_keepStrongestWindowPerLens() {
    // 同 lens 兩窗重疊入同群 → contributions 只留最強那個
    val a = lens("a", mapOf(ym(3) to 0.6, ym(4) to 0.9), listOf(win(ym(3), ym(4), 0.9), win(ym(4), ym(4), 0.6)))
    val m = RangePeaks.assemble(listOf(a), jan, dec)
    assertEquals(0.9, m.peaks.single().contributions.getValue("a").strength)
  }

  @Test
  fun troughs_excludePeakCoveredMonths() {
    val a = lens("a", mapOf(ym(2) to 0.8), listOf(win(ym(2), ym(4), 0.8)))   // 峰覆蓋 2..4 月
    val b = lens("b", mapOf(ym(9) to 0.5), listOf(win(ym(9), ym(9), 0.5)))
    val m = RangePeaks.assemble(listOf(a, b), jan, dec)

    // 原始谷:1、3..8、10..12(2 與 9 有能量);扣除峰覆蓋 {2,3,4,9} → 1、5..8、10..12
    assertEquals(
      listOf(TroughSpan(ym(1), ym(1)), TroughSpan(ym(5), ym(8)), TroughSpan(ym(10), ym(12))),
      m.troughs,
    )
    // 不變式:峰與谷永不重疊
    val peakMonths = m.peaks.flatMap { p -> generateSequence(p.from) { it.plusMonths(1) }.takeWhile { !it.isAfter(p.to) } }.toSet()
    val troughMonths = m.troughs.flatMap { t -> generateSequence(t.from) { it.plusMonths(1) }.takeWhile { !it.isAfter(t.to) } }.toSet()
    assertTrue(peakMonths.intersect(troughMonths).isEmpty())
  }

  @Test
  fun quarterRange_noYearAssumption() {
    // 季度窗(3 個月):同一組裝邏輯,無任何一年假設
    val a = lens("a", mapOf(ym(2) to 0.8), listOf(win(ym(2), ym(2), 0.8)))
    val m = RangePeaks.assemble(listOf(a), ym(1), ym(3))
    assertEquals(1, m.peaks.size)
    assertEquals(listOf(TroughSpan(ym(1), ym(1)), TroughSpan(ym(3), ym(3))), m.troughs)
  }
}
