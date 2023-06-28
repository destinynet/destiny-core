/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:59:49
 */
package destiny.core.calendar.eightwords

import destiny.core.News
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class MidnightLmtImplTest {

  private val julDayResolver = JulDayResolver1582CutoverImpl()

  @Test
  fun testGetNextMidnight() {

    val impl = MidnightLmtImpl(julDayResolver)
    val location = Location(News.EastWest.EAST, 121, 0, 0.0, News.NorthSouth.NORTH, 25, 0, 0.0, "Asia/Taipei")
    var expected: ChronoLocalDateTime<*>
    var actual: ChronoLocalDateTime<*>

    var lmt = LocalDateTime.of(2004, 12, 6, 14, 10, 0)
    actual = impl.getNextMidnight(lmt, location, julDayResolver)
    expected = LocalDateTime.of(2004, 12, 7, 0, 0, 0)
    assertEquals(expected, actual)

    lmt = LocalDateTime.of(2004, 12, 31, 0, 0, 0)
    actual = impl.getNextMidnight(lmt, location, julDayResolver)
    expected = LocalDateTime.of(2005, 1, 1, 0, 0, 0)
    assertEquals(expected, actual)
  }
}
