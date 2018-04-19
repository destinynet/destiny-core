/**
 * Created by smallufo on 2017-10-03.
 */
package destiny.core.calendar

import org.slf4j.LoggerFactory
import org.threeten.extra.chrono.JulianDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class JulianDateTimeTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * 1970-01-01 0:00 GMT ,  epoch sec = 0
   * 轉為 Julian date , 減去 14天
   */
  @Test
  fun ofEpochSecond() {
    val jdt = JulianDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)
    assertEquals(JulianDateTime.of(1969, 12, 19, 0, 0), jdt)
    assertEquals(JulianDate.of(1969, 12, 19).atTime(LocalTime.of(0, 0)), jdt)
  }


  /**
   * 西元 1970-01-01 的 Jul Greg 互相轉換 (相差14天)
   * <pre>
   * Greg          Julian
   * 1970-01-14     1970-01-01
  </pre> *
   */
  @Test
  fun testEpoch_Jul2Greg() {
    val jd = JulianDateTime.of(1970, 1, 1, 0, 0)
    // Julian -> Greg
    assertEquals(LocalDateTime.of(1970, 1, 14, 0, 0), LocalDateTime.from(jd))
  }


  /**
   * 西元 1970-01-01 的 Jul Greg 互相轉換 (相差14天)
   * <pre>
   * Greg          Julian
   * 1970-01-01     1969-12-19
  </pre> *
   */
  @Test
  fun testEpoch_Greg2Jul() {
    val gregTime = LocalDateTime.of(1970, 1, 1, 0, 0)

    val fromGregTime = JulianDateTime.from(gregTime)
    val julianTime = JulianDateTime.of(1969, 12, 19, 0, 0)

    // Greg -> Julian
    assertEquals(fromGregTime, julianTime)
  }
}