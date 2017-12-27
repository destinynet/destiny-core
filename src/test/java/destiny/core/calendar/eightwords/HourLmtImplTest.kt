/**
 * Created by smallufo on 2017-02-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HourLmtImplTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun getLmtNextStartOf() {
    val hourImpl = HourLmtImpl()

    val loc = Location.of(Locale.TAIWAN)

    val 子時前一秒 = LocalDateTime.of(2017, 2, 12, 22, 59, 0)
    assertEquals(LocalDateTime.of(2017, 2, 12, 23, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.子, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 1, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.丑, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 3, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.寅, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 5, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.卯, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 7, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.辰, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 9, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.巳, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 11, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.午, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 13, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.未, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 15, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.申, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 17, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.酉, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 19, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.戌, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 21, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, Branch.亥, revJulDayFunc))
  }

  companion object {

    private val revJulDayFunc =  {it:Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}