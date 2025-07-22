/**
 * Created by smallufo on 2025-07-23.
 */
package destiny.core.calendar.chinese

import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class YearMonthRangeTest {

  @Test
  fun testMerge() {
    val events = listOf(
      YearMonth.of(2025, 1),
      YearMonth.of(2025, 3),
      YearMonth.of(2025, 9)
    )

    val merged: List<YearMonthRange> = events.groupMergedRanges()
    assertEquals(
      listOf(
        YearMonthRange(YearMonth.of(2024, 12), YearMonth.of(2025, 4)),
        YearMonthRange(YearMonth.of(2025, 8), YearMonth.of(2025, 10)),
      ), merged
    )
  }
}
