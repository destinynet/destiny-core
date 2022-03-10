/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:10:50
 */
package destiny.core.calendar

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.chrono.IsoEra
import kotlin.test.Test
import kotlin.test.assertEquals

class DateHourMinSecDecoratorEnglishTest {

  @Test
  fun testGetOutputString() {
    val decorator = DateHourMinSecDecoratorEnglish

    decorator.getOutputString(LocalDateTime.of(2000, 1, 1, 0, 0, 0)).also {
      assertEquals("2000AD 01/01 00:00:00.00", it)
    }

    decorator.getOutputString(LocalDateTime.of(LocalDate.of(2000, 12, 31).with(IsoEra.BCE), LocalTime.of(23, 59, 59, 999000000))).also {
      assertEquals("2000BC 12/31 23:59:59.99", it)
    }

  }

}
