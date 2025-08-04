/**
 * Created by smallufo on 2025-08-04.
 */
package destiny.core.astrology

import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeLineEventsModelTest {

  @Test
  fun testGroupAdjacentEvents() {
    val events = listOf(
      YearMonthEvent(YearMonth.of(2018, 12), "事件A"),
      YearMonthEvent(YearMonth.of(2019, 2), "事件B"), // 與事件A鄰近 (因為 12月+1月+2月 連續)

      YearMonthEvent(YearMonth.of(2022, 5), "事件C"), // 獨立事件

      YearMonthEvent(YearMonth.of(2022, 11), "事件D"),
      YearMonthEvent(YearMonth.of(2022, 12), "事件E") // 與事件D鄰近
    )

    events.groupAdjacentEvents(extMonth = 1).also { groups ->
      assertEquals(3, groups.size)
      val expected = listOf(
        listOf(
          YearMonthEvent(YearMonth.of(2018, 12), "事件A"),
          YearMonthEvent(YearMonth.of(2019, 2), "事件B")
        ),
        listOf(YearMonthEvent(YearMonth.of(2022, 5), "事件C")),

        listOf(
          YearMonthEvent(YearMonth.of(2022, 11), "事件D"),
          YearMonthEvent(YearMonth.of(2022, 12), "事件E")
        )
      )
      assertEquals(expected, groups)
    }
  }
}
