/**
 * Created by smallufo on 2017-10-01.
 */
package destiny.core.calendar

import org.threeten.extra.chrono.JulianDate
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 西元 1582 測試
 * 已知： [J] 1582-10-04 cutover 到 [G] 1582-10-15
 *
 * 1582-10-15 子夜的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
 *
 * 則： (以下皆測試當日凌晨 0:00)
 * epoch day  |   julian day
 * d1 : [J] 1582-10-03         == [G] 1582-10-13 (不存在)  |   -141429   |   2299158.5
 * d2 : [J] 1582-10-04         == [G] 1582-10-14 (不存在)  |   -141428   |   2299159.5
 * d3 : [J] 1582-10-05 (不存在) == [G] 1582-10-15          |   -141427   |   2299160.5
 * d4 : [J] 1582-10-06 (不存在) == [G] 1582-10-16          |   -141426   |   2299161.5
 */
class TimeToolsYear1582Test {

  /**
   * Gregorian 第一天 : 1582-10-15
   */
  private val firstDayOfGregorian = Constants.CutOver1582.JULIAN_DAY

  private val d1G = LocalDateTime.of(1582, 10, 13, 0, 0)
  private val d2G = LocalDateTime.of(1582, 10, 14, 0, 0)
  private val d3G = LocalDateTime.of(1582, 10, 15, 0, 0)  // cutover 開始
  private val d4G = LocalDateTime.of(1582, 10, 16, 0, 0)

  private val d1J = JulianDateTime.of(1582, 10, 3, 0, 0)
  private val d2J = JulianDateTime.of(1582, 10, 4, 0, 0)
  private val d3J = JulianDateTime.of(1582, 10, 5, 0, 0)  // cutover 開始
  private val d4J = JulianDateTime.of(1582, 10, 6, 0, 0)

  /**
   * 四組日期，用 [java.time.Duration] 來取得間距
   */
  @Test
  fun testDuration() {
    // 四對日期，沒有任何秒差，代表相等
    assertEquals(0, Duration.between(d1G, d1J).seconds)
    assertEquals(0, Duration.between(d2G, d2J).seconds)
    assertEquals(0, Duration.between(d3G, d3J).seconds)
    assertEquals(0, Duration.between(d4G, d4J).seconds)

    // 相差一天
    assertEquals((60 * 60 * 24).toLong(), Duration.between(d1G, d2J).seconds)
    assertEquals((60 * 60 * 24).toLong(), Duration.between(d2G, d3J).seconds)
    assertEquals((60 * 60 * 24).toLong(), Duration.between(d3G, d4J).seconds)
    // Duration 有負數
    assertEquals((-60 * 60 * 24).toLong(), Duration.between(d4G, d3J).seconds)
    assertEquals((-60 * 60 * 24).toLong(), Duration.between(d3G, d2J).seconds)
    assertEquals((-60 * 60 * 24).toLong(), Duration.between(d2G, d1J).seconds)
  }

  /**
   * 四組日期的 jul day 應該相等
   * [TimeTools.getGmtJulDay]
   */
  @Test
  fun testGmtJulDayEquals() {
    assertEquals(TimeTools.getGmtJulDay(d1J), TimeTools.getGmtJulDay(d1G))
    assertEquals(TimeTools.getGmtJulDay(d2J), TimeTools.getGmtJulDay(d2G))
    assertEquals(TimeTools.getGmtJulDay(d3J), TimeTools.getGmtJulDay(d3G))
    assertEquals(TimeTools.getGmtJulDay(d4J), TimeTools.getGmtJulDay(d4G))

    assertEquals(firstDayOfGregorian - 2, TimeTools.getGmtJulDay(d1J))
    assertEquals(firstDayOfGregorian - 1, TimeTools.getGmtJulDay(d2J))
    assertEquals(firstDayOfGregorian, TimeTools.getGmtJulDay(d3J))  // cutover 開始
    assertEquals(firstDayOfGregorian + 1, TimeTools.getGmtJulDay(d4J))

    assertEquals(firstDayOfGregorian - 2, TimeTools.getGmtJulDay(d1G))
    assertEquals(firstDayOfGregorian - 1, TimeTools.getGmtJulDay(d2G))
    assertEquals(firstDayOfGregorian, TimeTools.getGmtJulDay(d3G))  // cutover 開始
    assertEquals(firstDayOfGregorian + 1, TimeTools.getGmtJulDay(d4G))



    assertEquals(firstDayOfGregorian + 0.5, TimeTools.getGmtJulDay(d3J.plus(12, ChronoUnit.HOURS)))  // cutover 開始 + 12小時
    assertEquals(firstDayOfGregorian + 0.5, TimeTools.getGmtJulDay(d3G.plus(12, ChronoUnit.HOURS)))  // cutover 開始 + 12小時

    assertEquals(firstDayOfGregorian - 0.25, TimeTools.getGmtJulDay(d3J.minus(6, ChronoUnit.HOURS))) // cutover 開始 - 6小時
    assertEquals(firstDayOfGregorian - 0.25, TimeTools.getGmtJulDay(d3G.minus(6, ChronoUnit.HOURS))) // cutover 開始 - 6小時
  }

  /**
   * 從「日期」、「時間」分開物件，轉換成 julDay
   */
  @Test
  fun dateTime2JulDay() {
    assertEquals(firstDayOfGregorian, TimeTools.getGmtJulDay(LocalDate.of(1582, 10, 15), LocalTime.MIDNIGHT))
    assertEquals(firstDayOfGregorian - 1, TimeTools.getGmtJulDay(JulianDate.of(1582, 10, 4), LocalTime.MIDNIGHT))
  }


  /**
   * 確認四個日期的 toInstant() 相等
   */
  @Test
  fun testInstantEquals() {
    assertEquals(d1J.toInstant(ZoneOffset.UTC), d1G.toInstant(ZoneOffset.UTC))
    assertEquals(d2J.toInstant(ZoneOffset.UTC), d2G.toInstant(ZoneOffset.UTC))
    assertEquals(d3J.toInstant(ZoneOffset.UTC), d3G.toInstant(ZoneOffset.UTC))
    assertEquals(d4J.toInstant(ZoneOffset.UTC), d4G.toInstant(ZoneOffset.UTC))
  }

  /**
   * 確認四個日期轉換出來的 epoch second 相等
   */
  @Test
  fun testEpochSecondEquals() {
    assertEquals(Constants.CutOver1582.FROM_UNIXEPOCH_SECONDS - 60 * 60 * 24 * 2, d1J.toInstant(ZoneOffset.UTC).epochSecond)
    assertEquals(Constants.CutOver1582.FROM_UNIXEPOCH_SECONDS - 60 * 60 * 24, d2J.toInstant(ZoneOffset.UTC).epochSecond)
    assertEquals(Constants.CutOver1582.FROM_UNIXEPOCH_SECONDS, d3J.toInstant(ZoneOffset.UTC).epochSecond)
    assertEquals(Constants.CutOver1582.FROM_UNIXEPOCH_SECONDS + 60 * 60 * 24, d4J.toInstant(ZoneOffset.UTC).epochSecond)
  }

  /**
   * 確認 : 四組日期的 epoch day 相等
   */
  @Test
  fun testEpochDay() {
    assertEquals(d1J.toLocalDate().toEpochDay(), d1G.toLocalDate().toEpochDay())
    assertEquals(d2J.toLocalDate().toEpochDay(), d2G.toLocalDate().toEpochDay())
    assertEquals(d3J.toLocalDate().toEpochDay(), d3G.toLocalDate().toEpochDay())
    assertEquals(d4J.toLocalDate().toEpochDay(), d4G.toLocalDate().toEpochDay())

    // 比對四組日期的 epoch day
    assertEquals(-141429, d1J.toLocalDate().toEpochDay())
    assertEquals(-141428, d2J.toLocalDate().toEpochDay())
    assertEquals(-141427, d3J.toLocalDate().toEpochDay()) // cutover 開始
    assertEquals(-141426, d4J.toLocalDate().toEpochDay())
  }

  /**
   * 測試 [TimeTools.isBetween] , 不同曆法之間的日期可以正確判斷是否 between 兩日期之間
   */
  @Test
  fun testBetween() {
    // 同曆法 , 確認 : d2 位於 d1 與 d3 之間
    assertTrue(TimeTools.isBetween(d2J, d1J, d3J))
    assertTrue(TimeTools.isBetween(d2J, d3J, d1J))
    assertTrue(TimeTools.isBetween(d2G, d1G, d3G))
    assertTrue(TimeTools.isBetween(d2G, d3G, d1G))

    // 同曆法 , 確認 : d3 位於 d2 與 d4 之間
    assertTrue(TimeTools.isBetween(d3J, d2J, d4J))
    assertTrue(TimeTools.isBetween(d3J, d4J, d2J))
    assertTrue(TimeTools.isBetween(d3G, d2G, d4G))
    assertTrue(TimeTools.isBetween(d3G, d4G, d2G))

    // 跨曆法 , 確認 : d2 位於 d1 與 d3 之間
    assertTrue(TimeTools.isBetween(d2G, d1J, d3J))
    assertTrue(TimeTools.isBetween(d2G, d3J, d1J))
    assertTrue(TimeTools.isBetween(d2J, d1G, d3G))
    assertTrue(TimeTools.isBetween(d2J, d3G, d1G))

    // 跨曆法 , 確認 : d3 位於 d2 與 d4 之間
    assertTrue(TimeTools.isBetween(d3J, d2G, d4G))
    assertTrue(TimeTools.isBetween(d3J, d4G, d2G))
    assertTrue(TimeTools.isBetween(d3G, d2J, d4J))
    assertTrue(TimeTools.isBetween(d3G, d4J, d2J))
  }

  /** 測試 [TimeTools.isAfter] 能否比對不同曆法之間的日期  */
  @Test
  fun testAfter() {
    // 同曆法 (J)
    assertTrue(TimeTools.isAfter(d2J, d1J))
    assertTrue(TimeTools.isAfter(d3J, d2J))
    assertTrue(TimeTools.isAfter(d4J, d3J))
    // 同曆法 (G)
    assertTrue(TimeTools.isAfter(d2G, d1G))
    assertTrue(TimeTools.isAfter(d3G, d2G))
    assertTrue(TimeTools.isAfter(d4G, d3G))

    // 不同曆法
    assertTrue(TimeTools.isAfter(d2G, d1J))
    assertTrue(TimeTools.isAfter(d3G, d2J))
    assertTrue(TimeTools.isAfter(d4G, d3J))

    assertTrue(TimeTools.isAfter(d2J, d1G))
    assertTrue(TimeTools.isAfter(d3J, d2G))
    assertTrue(TimeTools.isAfter(d4J, d3G))
  }

  /** 測試 [TimeTools.isBefore] 能否比對不同曆法之間的日期  */
  @Test
  fun testBefore() {
    // 同曆法 (J)
    assertTrue(TimeTools.isBefore(d1J, d2J))
    assertTrue(TimeTools.isBefore(d2J, d3J))
    assertTrue(TimeTools.isBefore(d3J, d4J))
    // 同曆法 (G)
    assertTrue(TimeTools.isBefore(d1G, d2G))
    assertTrue(TimeTools.isBefore(d2G, d3G))
    assertTrue(TimeTools.isBefore(d3G, d4G))

    // 不同曆法
    assertTrue(TimeTools.isBefore(d1J, d2G))
    assertTrue(TimeTools.isBefore(d2J, d3G))
    assertTrue(TimeTools.isBefore(d3J, d4G))

    assertTrue(TimeTools.isBefore(d1G, d2J))
    assertTrue(TimeTools.isBefore(d2G, d3J))
    assertTrue(TimeTools.isBefore(d3G, d4J))
  }
}
