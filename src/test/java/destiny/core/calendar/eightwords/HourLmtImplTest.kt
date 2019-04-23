/**
 * Created by smallufo on 2017-02-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HourLmtImplTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testLmtNextStartOf() {
    val hourImpl = HourLmtImpl()

    val loc = Location.of(Locale.TAIWAN)

    val 子時前一秒 = LocalDateTime.of(2017, 2, 12, 22, 59, 0)
    assertEquals(LocalDateTime.of(2017, 2, 12, 23, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 子, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  1, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 丑, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  3, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 寅, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  5, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 卯, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  7, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 辰, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  9, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 巳, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 11, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 午, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 13, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 未, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 15, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 申, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 17, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 酉, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 19, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 戌, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 21, 0, 0), hourImpl.getLmtNextStartOf(子時前一秒, loc, 亥, revJulDayFunc))

    val 子時後一秒 = LocalDateTime.of(2017, 2, 12, 23, 0, 1)
    assertEquals(LocalDateTime.of(2017, 2, 13, 23, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 子, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  1, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 丑, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  3, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 寅, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  5, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 卯, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  7, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 辰, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13,  9, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 巳, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 11, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 午, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 13, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 未, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 15, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 申, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 17, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 酉, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 19, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 戌, revJulDayFunc))
    assertEquals(LocalDateTime.of(2017, 2, 13, 21, 0, 0), hourImpl.getLmtNextStartOf(子時後一秒, loc, 亥, revJulDayFunc))
  }

  /**
   * 逆推
   */
  @Test
  fun testLmtPrevStartOf() {
    val hourImpl = HourLmtImpl()
    val loc = Location.of(Locale.TAIWAN)

    val 子時後一秒 = LocalDateTime.of(2019, 4, 23, 23, 0, 1)
    assertEquals(LocalDateTime.of(2019, 4, 23, 23, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 子, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  1, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 丑, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  3, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 寅, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  5, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 卯, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  7, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 辰, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  9, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 巳, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 11, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 午, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 13, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 未, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 15, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 申, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 17, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 酉, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 19, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 戌, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 21, 0, 0), hourImpl.getLmtPrevStartOf(子時後一秒, loc, 亥, revJulDayFunc))

    val 丑時後一秒 = LocalDateTime.of(2019, 4, 24, 1, 0, 1)
    assertEquals(LocalDateTime.of(2019, 4, 23, 23, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 子, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 24,  1, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 丑, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  3, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 寅, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  5, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 卯, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  7, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 辰, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  9, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 巳, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 11, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 午, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 13, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 未, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 15, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 申, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 17, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 酉, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 19, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 戌, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 21, 0, 0), hourImpl.getLmtPrevStartOf(丑時後一秒, loc, 亥, revJulDayFunc))

    val 子時前一秒 = LocalDateTime.of(2019, 4, 23, 22, 59, 59)
    assertEquals(LocalDateTime.of(2019, 4, 22, 23, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 子, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  1, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 丑, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  3, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 寅, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  5, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 卯, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  7, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 辰, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23,  9, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 巳, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 11, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 午, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 13, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 未, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 15, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 申, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 17, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 酉, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 19, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 戌, revJulDayFunc))
    assertEquals(LocalDateTime.of(2019, 4, 23, 21, 0, 0), hourImpl.getLmtPrevStartOf(子時前一秒, loc, 亥, revJulDayFunc))
  }

  companion object {

    private val revJulDayFunc =  {it:Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}