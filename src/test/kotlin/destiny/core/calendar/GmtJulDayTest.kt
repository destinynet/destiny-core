/**
 * Created by smallufo on 2021-08-04.
 */
package destiny.core.calendar

import kotlin.test.Test
import kotlin.test.assertEquals

internal class GmtJulDayTest {

  @Test
  fun testSort() {
    val a = GmtJulDay(0.0)
    val b = GmtJulDay(-1.0)
    val c = GmtJulDay(1.0)

    assertEquals(listOf(b, a, c), setOf(a, b, c).sorted())
  }
}
