/**
 * Created by smallufo on 2017-03-06.
 */
package destiny.core.calendar

import destiny.tools.KotlinLogging
import org.threeten.extra.chrono.JulianDate
import java.time.LocalDate
import java.time.temporal.ChronoField.YEAR
import java.time.temporal.ChronoField.YEAR_OF_ERA
import java.time.temporal.ChronoUnit
import java.time.temporal.JulianFields.JULIAN_DAY
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 可以比對的資訊
 * http://www.stevemorse.org/jcal/julian.html
 */
class JulianDateTest {

  private val logger = KotlinLogging.logger { }

  /**
   * 蘇軾 1037年1月8日 , 農曆 宋仁宗景祐三年十二月十九日
   */
  @Test
  fun 蘇軾() {
    val jd = JulianDate.of(1037 , 1 , 8)
    val gd = LocalDate.from(jd) // 1037-01-14
    logger.info("gd = {}" , gd)
  }

  /**
   * 取得今日的 epoch day , 不論是 JD 還是 LocalDate , 都應該相等
   */
  @Test
  fun test_epochDay_equals() {
    val jd = JulianDate.now()
    logger.debug("now = {} , epochDay = {}", jd, jd.toEpochDay())

    val ld = LocalDate.now()
    assertEquals(ld.toEpochDay(), jd.toEpochDay())
  }

  /**
   * Julian day epoch 測試
   * 已知： epoch 位於
   * January 1, 4713 BC 中午 :  proleptic Julian calendar
   * November 24, 4714 BC    : proleptic Gregorian calendar
   */
  @Test
  fun testAstroJulDayNumber() {
    val jd = JulianDate.of(-4712, 1, 1)
    val gd = LocalDate.of(-4713, 11, 24)

    assertEquals(jd.toEpochDay(), gd.toEpochDay())
    logger.info("start of julian day , epoch = {}", jd.toEpochDay())
  }


  /**
   * 西元 1 年 1 月 1 日，減一天
   */
  @Test
  fun test_CE_to_BC() {
    val jd = JulianDate.of(1, 1, 1)
    jd.getLong(JULIAN_DAY)

    val jd2 = jd.minus(1, ChronoUnit.DAYS)


    assertEquals(1, jd2.get(YEAR_OF_ERA).toLong()) // 西元前一年 , 輸出為 1 , YEAR_OF_ERA 一定大於 0
    assertEquals(0, jd2.get(YEAR).toLong())       // 西元前一年 , 連續值為 0
    logger.info("jd2 = {} . getYearOfEra = {} , getProlepticYear = {}", jd2, jd2.get(YEAR_OF_ERA), jd2.get(YEAR))

    // 西元1年1月1日，減一天 , 就是 0年12月31日 (proleptic)
    val jd3 = JulianDate.of(0, 12, 31)
    assertEquals(jd3, jd2)
  }

  /**
   * 資料來自 [java.time.temporal.JulianFields.JULIAN_DAY]
   * 實際天文計算的値，需要減 0.5
   * <pre>
   * | ISO date          |  Julian Day Number | Astronomical Julian Day |
   * | 1970-01-01T00:00  |         2,440,588  |         2,440,587.5     |
   * | 1970-01-01T06:00  |         2,440,588  |         2,440,587.75    |
   * | 1970-01-01T12:00  |         2,440,588  |         2,440,588.0     |
   * | 1970-01-01T18:00  |         2,440,588  |         2,440,588.25    |
   * | 1970-01-02T00:00  |         2,440,589  |         2,440,588.5     |
   * | 1970-01-02T06:00  |         2,440,589  |         2,440,588.75    |
   * | 1970-01-02T12:00  |         2,440,589  |         2,440,589.0     |
  </pre> *
   */

  @Test
  fun testEpoch1970() {
    var ld = LocalDate.of(1970, 1, 1)
    logger.info("ld.jd = {}", ld.getLong(JULIAN_DAY))

    val jd = JulianDate.of(1970, 1, 1)
    logger.info("jd.jd = {}", jd.getLong(JULIAN_DAY))

    ld = LocalDate.from(jd)
    assertEquals(LocalDate.of(1970, 1, 14), ld)
  }

  /**
   * 西元 1970-01-01 的 Jul Greg 互相轉換 (相差14天)
   * <pre>
   * Greg          Julian
   * 1970-01-01     1969-12-19
   *
   * 1970-01-14     1970-01-01
  </pre> *
   */
  @Test
  fun testEpoch_Jul2Greg() {
    val gd = LocalDate.of(1970, 1, 1)
    // Greg -> Julian
    assertEquals(JulianDate.of(1969, 12, 19), JulianDate.from(gd))

    val jd = JulianDate.of(1970, 1, 1)
    // Julian -> Greg
    assertEquals(LocalDate.of(1970, 1, 14), LocalDate.from(jd))
  }

  /**
   * 測試 JulianDate 與 GregorianDate 的 epochDay
   *
   * 利用這裡比對 http://www.stevemorse.org/jcal/julian.html
   *
   * 測試資料：
   * https://en.wikipedia.org/wiki/Conversion_between_Julian_and_Gregorian_calendars
   */
  @Test
  fun testEpochDay() {
    // 已知 G(2017, 1, 19) => J(2017,1,6)
    assertEquals(LocalDate.of(2017, 1, 19).toEpochDay(), JulianDate.of(2017, 1, 6).toEpochDay())

    // 西元前 501年
    assertEquals(JulianDate.of(-500, 3, 5).toEpochDay(), LocalDate.of(-500, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(-500, 3, 6).toEpochDay(), LocalDate.of(-500, 3, 1).toEpochDay())

    // 西元前 301年
    assertEquals(JulianDate.of(-300, 3, 3).toEpochDay(), LocalDate.of(-300, 2, 27).toEpochDay())
    assertEquals(JulianDate.of(-300, 3, 4).toEpochDay(), LocalDate.of(-300, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(-300, 3, 5).toEpochDay(), LocalDate.of(-300, 3, 1).toEpochDay())

    // 西元前 201年
    assertEquals(JulianDate.of(-200, 3, 2).toEpochDay(), LocalDate.of(-200, 2, 27).toEpochDay())
    assertEquals(JulianDate.of(-200, 3, 3).toEpochDay(), LocalDate.of(-200, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(-200, 3, 4).toEpochDay(), LocalDate.of(-200, 3, 1).toEpochDay())

    // 西元前 101年
    assertEquals(JulianDate.of(-100, 3, 1).toEpochDay(), LocalDate.of(-100, 2, 27).toEpochDay())
    assertEquals(JulianDate.of(-100, 3, 2).toEpochDay(), LocalDate.of(-100, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(-100, 3, 3).toEpochDay(), LocalDate.of(-100, 3, 1).toEpochDay())


    assertEquals(JulianDate.of(100, 2, 29).toEpochDay(), LocalDate.of(100, 2, 27).toEpochDay())
    assertEquals(JulianDate.of(100, 3, 1).toEpochDay(), LocalDate.of(100, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(100, 3, 2).toEpochDay(), LocalDate.of(100, 3, 1).toEpochDay())

    assertEquals(JulianDate.of(200, 2, 28).toEpochDay(), LocalDate.of(200, 2, 27).toEpochDay())
    assertEquals(JulianDate.of(200, 2, 29).toEpochDay(), LocalDate.of(200, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(200, 3, 1).toEpochDay(), LocalDate.of(200, 3, 1).toEpochDay())  // 日期相同 , 往下維持 100年！？

    assertEquals(JulianDate.of(300, 2, 28).toEpochDay(), LocalDate.of(300, 2, 28).toEpochDay()) // 日期相同
    assertEquals(JulianDate.of(300, 2, 29).toEpochDay(), LocalDate.of(300, 3, 1).toEpochDay())
    assertEquals(JulianDate.of(300, 3, 1).toEpochDay(), LocalDate.of(300, 3, 2).toEpochDay())

    assertEquals(JulianDate.of(500, 2, 28).toEpochDay(), LocalDate.of(500, 3, 1).toEpochDay())
    assertEquals(JulianDate.of(500, 2, 29).toEpochDay(), LocalDate.of(500, 3, 2).toEpochDay())
    assertEquals(JulianDate.of(500, 3, 1).toEpochDay(), LocalDate.of(500, 3, 3).toEpochDay())

    assertEquals(JulianDate.of(500, 2, 28).toEpochDay(), LocalDate.of(500, 3, 1).toEpochDay())
    assertEquals(JulianDate.of(500, 2, 29).toEpochDay(), LocalDate.of(500, 3, 2).toEpochDay())
    assertEquals(JulianDate.of(500, 3, 1).toEpochDay(), LocalDate.of(500, 3, 3).toEpochDay())

    assertEquals(JulianDate.of(600, 2, 28).toEpochDay(), LocalDate.of(600, 3, 2).toEpochDay())
    assertEquals(JulianDate.of(600, 2, 29).toEpochDay(), LocalDate.of(600, 3, 3).toEpochDay())
    assertEquals(JulianDate.of(600, 3, 1).toEpochDay(), LocalDate.of(600, 3, 4).toEpochDay())

    assertEquals(JulianDate.of(700, 2, 28).toEpochDay(), LocalDate.of(700, 3, 3).toEpochDay())
    assertEquals(JulianDate.of(700, 2, 29).toEpochDay(), LocalDate.of(700, 3, 4).toEpochDay())
    assertEquals(JulianDate.of(700, 3, 1).toEpochDay(), LocalDate.of(700, 3, 5).toEpochDay())

    assertEquals(JulianDate.of(900, 2, 28).toEpochDay(), LocalDate.of(900, 3, 4).toEpochDay())
    assertEquals(JulianDate.of(900, 2, 29).toEpochDay(), LocalDate.of(900, 3, 5).toEpochDay())
    assertEquals(JulianDate.of(900, 3, 1).toEpochDay(), LocalDate.of(900, 3, 6).toEpochDay())

    assertEquals(JulianDate.of(1000, 2, 28).toEpochDay(), LocalDate.of(1000, 3, 5).toEpochDay())
    assertEquals(JulianDate.of(1000, 2, 29).toEpochDay(), LocalDate.of(1000, 3, 6).toEpochDay())
    assertEquals(JulianDate.of(1000, 3, 1).toEpochDay(), LocalDate.of(1000, 3, 7).toEpochDay())

    assertEquals(JulianDate.of(1100, 2, 28).toEpochDay(), LocalDate.of(1100, 3, 6).toEpochDay())
    assertEquals(JulianDate.of(1100, 2, 29).toEpochDay(), LocalDate.of(1100, 3, 7).toEpochDay())
    assertEquals(JulianDate.of(1100, 3, 1).toEpochDay(), LocalDate.of(1100, 3, 8).toEpochDay())

    assertEquals(JulianDate.of(1300, 2, 28).toEpochDay(), LocalDate.of(1300, 3, 7).toEpochDay())
    assertEquals(JulianDate.of(1300, 2, 29).toEpochDay(), LocalDate.of(1300, 3, 8).toEpochDay())
    assertEquals(JulianDate.of(1300, 3, 1).toEpochDay(), LocalDate.of(1300, 3, 9).toEpochDay())

    assertEquals(JulianDate.of(1400, 2, 28).toEpochDay(), LocalDate.of(1400, 3, 8).toEpochDay())
    assertEquals(JulianDate.of(1400, 2, 29).toEpochDay(), LocalDate.of(1400, 3, 9).toEpochDay())
    assertEquals(JulianDate.of(1400, 3, 1).toEpochDay(), LocalDate.of(1400, 3, 10).toEpochDay())

    assertEquals(JulianDate.of(1500, 2, 28).toEpochDay(), LocalDate.of(1500, 3, 9).toEpochDay())
    assertEquals(JulianDate.of(1500, 2, 29).toEpochDay(), LocalDate.of(1500, 3, 10).toEpochDay())
    assertEquals(JulianDate.of(1500, 3, 1).toEpochDay(), LocalDate.of(1500, 3, 11).toEpochDay())

    // 曆法 cutover : (J)1582-10-4 下一天 => (G)1582-10-15

    assertEquals(JulianDate.of(1582, 10, 4).toEpochDay(), LocalDate.of(1582, 10, 14).toEpochDay())
    // ^^^^^^^^^^ 歷史上其實此日期(含)之前不存在
    // ^^^^^^^^^ 1582-10-4 ，隔天就是 1582-10-15
    assertEquals(JulianDate.of(1582, 10, 5).toEpochDay(), LocalDate.of(1582, 10, 15).toEpochDay())
    // ^^^^^^^^^ 歷史上此日期（含）之後不存在
    assertEquals(JulianDate.of(1582, 10, 6).toEpochDay(), LocalDate.of(1582, 10, 16).toEpochDay())


    assertEquals(JulianDate.of(1700, 2, 18).toEpochDay(), LocalDate.of(1700, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(1700, 2, 19).toEpochDay(), LocalDate.of(1700, 3, 1).toEpochDay())
    assertEquals(JulianDate.of(1700, 2, 28).toEpochDay(), LocalDate.of(1700, 3, 10).toEpochDay())
    assertEquals(JulianDate.of(1700, 2, 29).toEpochDay(), LocalDate.of(1700, 3, 11).toEpochDay())
    assertEquals(JulianDate.of(1700, 3, 1).toEpochDay(), LocalDate.of(1700, 3, 12).toEpochDay())

    assertEquals(JulianDate.of(1800, 2, 17).toEpochDay(), LocalDate.of(1800, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(1800, 2, 18).toEpochDay(), LocalDate.of(1800, 3, 1).toEpochDay())
    assertEquals(JulianDate.of(1800, 2, 28).toEpochDay(), LocalDate.of(1800, 3, 11).toEpochDay())
    assertEquals(JulianDate.of(1800, 2, 29).toEpochDay(), LocalDate.of(1800, 3, 12).toEpochDay())
    assertEquals(JulianDate.of(1800, 3, 1).toEpochDay(), LocalDate.of(1800, 3, 13).toEpochDay())

    assertEquals(JulianDate.of(1900, 2, 16).toEpochDay(), LocalDate.of(1900, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(1900, 2, 17).toEpochDay(), LocalDate.of(1900, 3, 1).toEpochDay())
    assertEquals(JulianDate.of(1900, 2, 28).toEpochDay(), LocalDate.of(1900, 3, 12).toEpochDay())
    assertEquals(JulianDate.of(1900, 2, 29).toEpochDay(), LocalDate.of(1900, 3, 13).toEpochDay())
    assertEquals(JulianDate.of(1900, 3, 1).toEpochDay(), LocalDate.of(1900, 3, 14).toEpochDay())

    assertEquals(JulianDate.of(2100, 2, 15).toEpochDay(), LocalDate.of(2100, 2, 28).toEpochDay())
    assertEquals(JulianDate.of(2100, 2, 16).toEpochDay(), LocalDate.of(2100, 3, 1).toEpochDay())
    assertEquals(JulianDate.of(2100, 2, 28).toEpochDay(), LocalDate.of(2100, 3, 13).toEpochDay())
    assertEquals(JulianDate.of(2100, 2, 29).toEpochDay(), LocalDate.of(2100, 3, 14).toEpochDay())
  }

}
