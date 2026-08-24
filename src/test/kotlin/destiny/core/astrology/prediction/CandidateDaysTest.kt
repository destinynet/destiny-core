/**
 * Created by Claude on 2026-08-25.
 */
package destiny.core.astrology.prediction

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

/**
 * [CandidateDays.cluster] 的不變式(Yearly Peaks B3-a,root docs/plans/2026-08-24 §3.5)。
 *
 * 純函式、假日期 —— 這裡守的是「密度極大值＋非極大抑制」的叢集語意,不是星曆。
 * ⚠️ 首版用「日差 ≤ gap 遞移連鎖」在真實密度(近 1 筆/天)下把數週黏成一段,
 * 已改密度定式;「高密度均勻背景不得黏成一大段」由本檔最後一條釘住。
 */
class CandidateDaysTest {

  private fun d(day: Int): LocalDate = LocalDate.of(2027, 3, 1).plusDays((day - 1).toLong())

  @Test
  fun `空輸入 → 空輸出,不丟例外`() {
    assertEquals(emptyList(), CandidateDays.cluster(emptyList()))
  }

  @Test
  fun `單一事件 → 單段,peakDay 即該日`() {
    assertEquals(listOf(CandidateDaySpan(d(5), d(5), d(5), 1)), CandidateDays.cluster(listOf(d(5))))
  }

  @Test
  fun `亂序輸入與排序輸入等價`() {
    val sorted = listOf(d(1), d(2), d(2), d(8))
    assertEquals(CandidateDays.cluster(sorted), CandidateDays.cluster(sorted.reversed()))
  }

  @Test
  fun `peakDay 是密度極大日,段界是窗內實際事件日`() {
    // 3/1,3/2,3/2,3/3 + 3/10;half=1 → 3/2 密度 4 最高,段 3/1..3/3;3/10 孤立段
    val spans = CandidateDays.cluster(listOf(d(1), d(2), d(2), d(3), d(10)), halfWindowDays = 1, topK = 3)
    assertEquals(
      listOf(CandidateDaySpan(d(1), d(3), d(2), 4), CandidateDaySpan(d(10), d(10), d(10), 1)),
      spans,
    )
  }

  @Test
  fun `同密取最早`() {
    // 3/1,3/2,3/3 皆密度 3(half=1 → 3/2)…改用孤立雙日:3/1 與 3/9 皆密度 1 → 皆入選,時序輸出
    val spans = CandidateDays.cluster(listOf(d(1), d(9)), halfWindowDays = 1, topK = 1)
    assertEquals(d(1), spans.single().peakDay, "同密度時抑制後只留最早者")
  }

  @Test
  fun `topK 依密度取段,輸出恢復時序`() {
    // 段A(3/1~3/2, 2筆)、段B(3/10, 1筆)、段C(3/20~3/22, 3筆);topK=2 → 留 C A,時序輸出 A C
    val spans = CandidateDays.cluster(
      listOf(d(1), d(2), d(10), d(20), d(21), d(22)),
      halfWindowDays = 2, topK = 2,
    )
    assertEquals(listOf(2, 3), spans.map { it.eventCount })
    assertTrue(spans[0].from < spans[1].from)
  }

  @Test
  fun `高密度均勻背景不得黏成一大段 —— 段窗有界且互不重疊`() {
    // 28 天每天 1 筆(實測快層的形狀):遞移連鎖會黏成單段 28 天;密度定式必須出多個 ±2 天窗
    val spans = CandidateDays.cluster((1..28).map { d(it) }, halfWindowDays = 2, topK = 5)
    assertTrue(spans.size > 1, "均勻背景不得只出一大段")
    spans.forEach { span ->
      assertTrue(ChronoUnit.DAYS.between(span.from, span.to) <= 4, "段寬不得超過 2×half:$span")
      assertTrue(!span.peakDay.isBefore(span.from) && !span.peakDay.isAfter(span.to))
      assertTrue(span.eventCount >= 1)
    }
    spans.zipWithNext().forEach { (a, b) -> assertTrue(a.to.isBefore(b.from), "段須時序不重疊:$a vs $b") }
  }

  @Test
  fun `參數防呆`() {
    assertFailsWith<IllegalArgumentException> { CandidateDays.cluster(listOf(d(1)), halfWindowDays = -1) }
    assertFailsWith<IllegalArgumentException> { CandidateDays.cluster(listOf(d(1)), topK = 0) }
  }
}
