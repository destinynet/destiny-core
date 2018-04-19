/**
 * Created by smallufo on 2017-10-03.
 */
package destiny.core.calendar

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDate
import java.util.Calendar
import java.util.GregorianCalendar

class GregorianCalendarTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * 西元 1582年， 10-04 的隔天，變為 10-15
   * GregorianCalendar 會處理此部分
   * 但是 LocalDate 為 proleptic Gregorian Calendar , 不會處理此部分
   */
  @Test
  fun test1582() {
    val gc = GregorianCalendar(1582, 9 - 1, 30)
    for (i in 1..10) {
      gc.add(Calendar.DAY_OF_YEAR, 1)
      val ld = gc.toZonedDateTime().toLocalDate()
      logger.info("[GC]{}-{}-{} \t [LocalDate]{}", gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1,
                  gc.get(Calendar.DAY_OF_MONTH), ld)
    }
  }
}
