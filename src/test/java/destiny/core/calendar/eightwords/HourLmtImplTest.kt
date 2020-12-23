/**
 * Created by smallufo on 2017-02-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.locationOf
import destiny.core.chinese.Branch.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HourLmtImplTest {

  @Test
  fun testGetDailyBranchStartMap() {
    val hourImpl = HourLmtImpl()
    val loc = locationOf(Locale.TAIWAN)

    hourImpl.getDailyBranchStartMap(LocalDate.of(2020, 12, 24), loc, revJulDayFunc).also { map ->
      assertEquals(LocalDateTime.of(2020, 12, 23, 23, 0), map[子])
      assertEquals(LocalDateTime.of(2020, 12, 24, 1, 0),  map[丑])
      assertEquals(LocalDateTime.of(2020, 12, 24, 3, 0),  map[寅])
      assertEquals(LocalDateTime.of(2020, 12, 24, 5, 0),  map[卯])
      assertEquals(LocalDateTime.of(2020, 12, 24, 7, 0),  map[辰])
      assertEquals(LocalDateTime.of(2020, 12, 24, 9, 0),  map[巳])
      assertEquals(LocalDateTime.of(2020, 12, 24, 11, 0), map[午])
      assertEquals(LocalDateTime.of(2020, 12, 24, 13, 0), map[未])
      assertEquals(LocalDateTime.of(2020, 12, 24, 15, 0), map[申])
      assertEquals(LocalDateTime.of(2020, 12, 24, 17, 0), map[酉])
      assertEquals(LocalDateTime.of(2020, 12, 24, 19, 0), map[戌])
      assertEquals(LocalDateTime.of(2020, 12, 24, 21, 0), map[亥])
    }
  }

  @Test
  fun testGetDailyBranchMiddleMap() {
    val hourImpl = HourLmtImpl()
    val loc = locationOf(Locale.TAIWAN)

    hourImpl.getDailyBranchMiddleMap(LocalDate.of(2020, 12, 24), loc, revJulDayFunc).also { map ->
      assertEquals(LocalDateTime.of(2020, 12, 24, 0, 0), map[子])
      assertEquals(LocalDateTime.of(2020, 12, 24, 2, 0), map[丑])
      assertEquals(LocalDateTime.of(2020, 12, 24, 4, 0), map[寅])
      assertEquals(LocalDateTime.of(2020, 12, 24, 6, 0), map[卯])
      assertEquals(LocalDateTime.of(2020, 12, 24, 8, 0), map[辰])
      assertEquals(LocalDateTime.of(2020, 12, 24, 10, 0), map[巳])
      assertEquals(LocalDateTime.of(2020, 12, 24, 12, 0), map[午])
      assertEquals(LocalDateTime.of(2020, 12, 24, 14, 0), map[未])
      assertEquals(LocalDateTime.of(2020, 12, 24, 16, 0), map[申])
      assertEquals(LocalDateTime.of(2020, 12, 24, 18, 0), map[酉])
      assertEquals(LocalDateTime.of(2020, 12, 24, 20, 0), map[戌])
      assertEquals(LocalDateTime.of(2020, 12, 24, 22, 0), map[亥])
    }
  }

  @Test
  fun testLmtNextStartOf() {
    val hourImpl = HourLmtImpl()

    val loc = locationOf(Locale.TAIWAN)

    LocalDateTime.of(2017, 2, 12, 22, 59, 0).also {
      // 子時前一秒
      assertEquals(LocalDateTime.of(2017, 2, 12, 23, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 子, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 1, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 丑, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 3, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 寅, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 5, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 卯, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 7, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 辰, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 9, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 巳, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 11, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 午, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 13, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 未, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 15, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 申, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 17, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 酉, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 19, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 戌, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 21, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 亥, revJulDayFunc))
    }

    LocalDateTime.of(2017, 2, 12, 23, 0, 1).also {
      // 子時後一秒
      assertEquals(LocalDateTime.of(2017, 2, 13, 23, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 子, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 1, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 丑, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 3, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 寅, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 5, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 卯, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 7, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 辰, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 9, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 巳, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 11, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 午, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 13, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 未, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 15, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 申, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 17, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 酉, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 19, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 戌, revJulDayFunc))
      assertEquals(LocalDateTime.of(2017, 2, 13, 21, 0, 0), hourImpl.getLmtNextStartOf(it, loc, 亥, revJulDayFunc))
    }

  }

  /**
   * 逆推
   */
  @Test
  fun testLmtPrevStartOf() {
    val hourImpl = HourLmtImpl()
    val loc = locationOf(Locale.TAIWAN)

    LocalDateTime.of(2019, 4, 23, 23, 0, 1).also {
      // 子時後一秒
      assertEquals(LocalDateTime.of(2019, 4, 23, 23, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 子, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 1, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 丑, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 3, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 寅, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 5, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 卯, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 7, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 辰, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 9, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 巳, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 11, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 午, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 13, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 未, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 15, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 申, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 17, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 酉, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 19, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 戌, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 21, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 亥, revJulDayFunc))
    }


    LocalDateTime.of(2019, 4, 24, 1, 0, 1).also {
      // 丑時後一秒
      assertEquals(LocalDateTime.of(2019, 4, 23, 23, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 子, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 24, 1, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 丑, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 3, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 寅, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 5, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 卯, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 7, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 辰, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 9, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 巳, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 11, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 午, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 13, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 未, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 15, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 申, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 17, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 酉, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 19, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 戌, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 21, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 亥, revJulDayFunc))
    }

    LocalDateTime.of(2019, 4, 23, 22, 59, 59).also {
      // 子時前一秒
      assertEquals(LocalDateTime.of(2019, 4, 22, 23, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 子, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 1, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 丑, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 3, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 寅, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 5, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 卯, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 7, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 辰, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 9, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 巳, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 11, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 午, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 13, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 未, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 15, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 申, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 17, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 酉, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 19, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 戌, revJulDayFunc))
      assertEquals(LocalDateTime.of(2019, 4, 23, 21, 0, 0), hourImpl.getLmtPrevStartOf(it, loc, 亥, revJulDayFunc))
    }

  }

  companion object {

    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
