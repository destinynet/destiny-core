/**
 * Created by smallufo on 2026-11-23.
 */
package destiny.core.calendar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ITimeSpanTest {

  private data class Span(override val fromTime: GmtJulDay, override val toTime: GmtJulDay) : ITimeSpan

  private fun span(from: Double, to: Double) = Span(GmtJulDay(from), GmtJulDay(to))

  /** 三段相接：[10, 20)、[20, 30)、[30, 40) */
  private val spans = listOf(span(10.0, 20.0), span(20.0, 30.0), span(30.0, 40.0))

  @Test
  fun `半開區間：起點算、終點不算`() {
    val s = spans[0]
    assertTrue(GmtJulDay(10.0) in s)
    assertTrue(GmtJulDay(19.999) in s)
    assertFalse(GmtJulDay(20.0) in s)
    assertFalse(GmtJulDay(9.999) in s)
  }

  @Test
  fun `交界那一刻屬於後一段`() {
    assertEquals(1, spans.indexAt(GmtJulDay(20.0)))
    assertEquals(spans[2], spans.spanAt(GmtJulDay(30.0)))
  }

  @Test
  fun `範圍外：indexAt 回 null，indexNear 取最靠近的那一頭`() {
    assertNull(spans.indexAt(GmtJulDay(5.0)))
    assertNull(spans.indexAt(GmtJulDay(40.0)))
    assertNull(spans.spanAt(GmtJulDay(99.0)))
    assertEquals(0, spans.indexNear(GmtJulDay(5.0)))
    assertEquals(2, spans.indexNear(GmtJulDay(40.0)))
    assertEquals(1, spans.indexNear(GmtJulDay(25.0)))
  }

  @Test
  fun `空隙裡不屬於任何一段`() {
    val gapped = listOf(span(10.0, 20.0), span(30.0, 40.0))
    assertNull(gapped.indexAt(GmtJulDay(25.0)))
    assertEquals(1, gapped.indexNear(GmtJulDay(25.0)))
  }
}
