/**
 * Created by smallufo on 2025-08-08.
 */
package destiny.core.astrology

import destiny.core.EventType
import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AbstractEventTest {

  @Test
  fun testGroupAdjacentEvents_MonthOnly() {
    val events = listOf(
      MonthEvent(YearMonth.of(2018, 12), EventType.OTHERS, "事件A"),
      MonthEvent(YearMonth.of(2019, 2), EventType.OTHERS, "事件B"), // 與事件A鄰近 (因為 12月+1月+2月 連續)

      MonthEvent(YearMonth.of(2022, 5), EventType.OTHERS, "事件C"), // 獨立事件

      MonthEvent(YearMonth.of(2022, 11), EventType.OTHERS, "事件D"),
      MonthEvent(YearMonth.of(2022, 12), EventType.OTHERS, "事件E") // 與事件D鄰近
    )

    events.groupAdjacentEvents(extMonth = 1).also { groups ->
      assertEquals(3, groups.size)
      val expected = listOf(
        listOf(
          MonthEvent(YearMonth.of(2018, 12), EventType.OTHERS, "事件A"),
          MonthEvent(YearMonth.of(2019, 2), EventType.OTHERS, "事件B")
        ),
        listOf(MonthEvent(YearMonth.of(2022, 5), EventType.OTHERS, "事件C")),

        listOf(
          MonthEvent(YearMonth.of(2022, 11), EventType.OTHERS, "事件D"),
          MonthEvent(YearMonth.of(2022, 12), EventType.OTHERS, "事件E")
        )
      )
      assertEquals(expected, groups)
    }
  }

  /**
   * 混合 MonthEvent 和 DayEvent
   */
  @Test
  fun testGroupAdjacentEvents_MixedTypes() {
    val events = listOf(
      MonthEvent(YearMonth.of(2025, 1), EventType.OTHERS, "升職"),
      DayEvent(LocalDate.of(2025, 3, 15), EventType.OTHERS, "婚禮"), // 3月與1月鄰近 (extMonth=1)

      MonthEvent(YearMonth.of(2025, 7), EventType.OTHERS, "搬家") // 7月與3月不鄰近
    )

    val groups = events.groupAdjacentEvents(extMonth = 1)
    assertEquals(2, groups.size)
    val expected = listOf(
      listOf(
        MonthEvent(YearMonth.of(2025, 1), EventType.OTHERS, "升職"),
        DayEvent(LocalDate.of(2025, 3, 15), EventType.OTHERS, "婚禮")
      ),
      listOf(
        MonthEvent(YearMonth.of(2025, 7), EventType.OTHERS, "搬家")
      )
    )
    assertEquals(expected, groups)
  }

  /**
   * 只包含 DayEvent，並測試跨年度
   */
  @Test
  fun testGroupAdjacentEvents_DayOnly_AcrossYears() {
    val events = listOf(
      DayEvent(LocalDate.of(2023, 1, 10), EventType.OTHERS, "A公司面試"),
      DayEvent(LocalDate.of(2023, 3, 5), EventType.OTHERS, "拿到A公司Offer"), // 3月與1月鄰近
      DayEvent(LocalDate.of(2023, 12, 25), EventType.OTHERS, "聖誕節車禍"),
      DayEvent(LocalDate.of(2024, 1, 30), EventType.OTHERS, "康復出院")  // 隔年1月與12月鄰近
    )

    val groups = events.groupAdjacentEvents(extMonth = 1)
    assertEquals(2, groups.size)
    val expected = listOf(
      listOf(
        DayEvent(LocalDate.of(2023, 1, 10), EventType.OTHERS, "A公司面試"),
        DayEvent(LocalDate.of(2023, 3, 5), EventType.OTHERS, "拿到A公司Offer")
      ),
      listOf(
        DayEvent(LocalDate.of(2023, 12, 25), EventType.OTHERS, "聖誕節車禍"),
        DayEvent(LocalDate.of(2024, 1, 30), EventType.OTHERS, "康復出院")
      )
    )
    assertEquals(expected, groups)
  }


  /**
   * 新測試：邊界條件 - 空列表
   */
  @Test
  fun testGroupAdjacentEvents_EmptyList() {
    val events = emptyList<AbstractEvent>()
    val groups = events.groupAdjacentEvents()
    assertEquals(1, groups.size)
    assertTrue { groups[0].isEmpty() }
  }

  /**
   * 新測試：邊界條件 - 單一事件
   */
  @Test
  fun testGroupAdjacentEvents_SingleEvent() {
    val events = listOf(MonthEvent(YearMonth.of(2022, 5), EventType.OTHERS, "單一事件"))
    val groups = events.groupAdjacentEvents()
    assertEquals(1, groups.size)
    assertEquals(listOf(events), groups)
  }

  /**
   * 新測試：測試 extMonth = 0 的情況
   * 只有在同一個月或相鄰月份的事件才會被分在同一組
   */
  @Test
  fun testGroupAdjacentEvents_ExtMonthZero() {
    val events = listOf(
      MonthEvent(YearMonth.of(2022, 1), EventType.OTHERS, "事件A"),
      MonthEvent(YearMonth.of(2022, 2), EventType.OTHERS, "事件B"), // 與A相鄰
      MonthEvent(YearMonth.of(2022, 4), EventType.OTHERS, "事件C")  // 與B不相鄰 (因為2月+0+4月不連續)
    )

    // extMonth = 0, range of 2022-02 is [2022-02, 2022-02]
    // range of 2022-04 is [2022-04, 2022-04]
    // 2022-04 is not after 2022-02.plus(1) -> 2022-03 , it's after. So new group
    val groups = events.groupAdjacentEvents(extMonth = 0)
    assertEquals(2, groups.size)
    val expected = listOf(
      listOf(
        MonthEvent(YearMonth.of(2022, 1), EventType.OTHERS, "事件A"),
        MonthEvent(YearMonth.of(2022, 2), EventType.OTHERS, "事件B")
      ),
      listOf(
        MonthEvent(YearMonth.of(2022, 4), EventType.OTHERS, "事件C")
      )
    )
    assertEquals(expected, groups)
  }

  /**
   * 新測試：測試 extMonth = 2 的情況
   * 更大的 extMonth 會讓更多事件被分在同一組
   */
  @Test
  fun testGroupAdjacentEvents_ExtMonthTwo() {
    val events = listOf(
      MonthEvent(YearMonth.of(2022, 1), EventType.OTHERS, "事件A"), // range: [2021-11, 2022-03]
      MonthEvent(YearMonth.of(2022, 4), EventType.OTHERS, "事件B"), // range: [2022-02, 2022-06], 與A重疊
      MonthEvent(YearMonth.of(2022, 8), EventType.OTHERS, "事件C")  // range: [2022-06, 2022-10], 與B重疊
    )
    val groups = events.groupAdjacentEvents(extMonth = 2)
    assertEquals(1, groups.size)
    assertEquals(listOf(events), groups)
  }
}
