/**
 * Created by smallufo on 2017-10-01.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.chrono.JulianDate;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;

import static destiny.core.calendar.TimeTools.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 西元 1582 測試
 * 已知： [J] 1582-10-04 cutover 到 [G] 1582-10-15
 *
 * 1582-10-15 子夜的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
 *
 * 則： (以下皆測試當日凌晨 0:00)
 *                                                           epoch day  |   julian day
 * d1 : [J] 1582-10-03         == [G] 1582-10-13 (不存在)  |   -141429   |   2299158.5
 * d2 : [J] 1582-10-04         == [G] 1582-10-14 (不存在)  |   -141428   |   2299159.5
 * d3 : [J] 1582-10-05 (不存在) == [G] 1582-10-15          |   -141427   |   2299160.5
 * d4 : [J] 1582-10-06 (不存在) == [G] 1582-10-16          |   -141426   |   2299161.5
 */
public class TimeToolsYear1582Test {

  /**
   * 西元 1582-10-15 0:0 的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
   */
  private final static long GREGORIAN_START_INSTANT = -12219292800L;

  /**
   * Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
   */
  private double firstDayOfGregorian = 2299160.5;

  private Logger logger = LoggerFactory.getLogger(getClass());

  private LocalDateTime d1G = LocalDateTime.of(1582, 10, 13, 0, 0);
  private LocalDateTime d2G = LocalDateTime.of(1582, 10, 14, 0, 0);
  private LocalDateTime d3G = LocalDateTime.of(1582, 10, 15, 0, 0);   // cutover 開始
  private LocalDateTime d4G = LocalDateTime.of(1582, 10, 16, 0, 0);

  private JulianDateTime d1J = JulianDateTime.of(1582, 10, 3, 0, 0);
  private JulianDateTime d2J = JulianDateTime.of(1582, 10, 4, 0, 0);
  private JulianDateTime d3J = JulianDateTime.of(1582, 10, 5, 0, 0);  // cutover 開始
  private JulianDateTime d4J = JulianDateTime.of(1582, 10, 6, 0, 0);

  /**
   * 四組日期，用 {@link java.time.Duration} 來取得間距
   */
  @Test
  public void testDuration() {
    // 四對日期，沒有任何秒差，代表相等
    assertEquals(0, Duration.between(d1G, d1J).getSeconds());
    assertEquals(0, Duration.between(d2G, d2J).getSeconds());
    assertEquals(0, Duration.between(d3G, d3J).getSeconds());
    assertEquals(0, Duration.between(d4G, d4J).getSeconds());

    // 相差一天
    assertEquals(60*60*24, Duration.between(d1G, d2J).getSeconds());
    assertEquals(60*60*24, Duration.between(d2G, d3J).getSeconds());
    assertEquals(60*60*24, Duration.between(d3G, d4J).getSeconds());
    // Duration 有負數
    assertEquals(-60*60*24, Duration.between(d4G, d3J).getSeconds());
    assertEquals(-60*60*24, Duration.between(d3G, d2J).getSeconds());
    assertEquals(-60*60*24, Duration.between(d2G, d1J).getSeconds());
  }

  /**
   * 四組日期的 jul day 應該相等
   * {@link TimeTools#getGmtJulDay(ChronoLocalDateTime)}
   */
  @Test
  public void testGmtJulDayEquals() {
    assertEquals(Companion.getGmtJulDay(d1J) , Companion.getGmtJulDay(d1G) , 0.0);
    assertEquals(Companion.getGmtJulDay(d2J) , Companion.getGmtJulDay(d2G) , 0.0);
    assertEquals(Companion.getGmtJulDay(d3J) , Companion.getGmtJulDay(d3G) , 0.0);
    assertEquals(Companion.getGmtJulDay(d4J) , Companion.getGmtJulDay(d4G) , 0.0);

    assertEquals(firstDayOfGregorian-2 , Companion.getGmtJulDay(d1J) , 0.0);
    assertEquals(firstDayOfGregorian-1 , Companion.getGmtJulDay(d2J) , 0.0);
    assertEquals(firstDayOfGregorian   , Companion.getGmtJulDay(d3J) , 0.0);  // cutover 開始
    assertEquals(firstDayOfGregorian+1 , Companion.getGmtJulDay(d4J) , 0.0);

    assertEquals(firstDayOfGregorian-2 , Companion.getGmtJulDay(d1G) , 0.0);
    assertEquals(firstDayOfGregorian-1 , Companion.getGmtJulDay(d2G) , 0.0);
    assertEquals(firstDayOfGregorian   , Companion.getGmtJulDay(d3G) , 0.0);  // cutover 開始
    assertEquals(firstDayOfGregorian+1 , Companion.getGmtJulDay(d4G) , 0.0);



    assertEquals(firstDayOfGregorian+0.5   , Companion.getGmtJulDay(d3J.plus(12 , ChronoUnit.HOURS)) , 0.0);  // cutover 開始 + 12小時
    assertEquals(firstDayOfGregorian+0.5   , Companion.getGmtJulDay(d3G.plus(12 , ChronoUnit.HOURS)) , 0.0);  // cutover 開始 + 12小時

    assertEquals(firstDayOfGregorian-0.25  , Companion.getGmtJulDay(d3J.minus(6 , ChronoUnit.HOURS)) , 0.0);  // cutover 開始 - 6小時
    assertEquals(firstDayOfGregorian-0.25  , Companion.getGmtJulDay(d3G.minus(6 , ChronoUnit.HOURS)) , 0.0);  // cutover 開始 - 6小時
  }

    /**
   * 從「日期」、「時間」分開物件，轉換成 julDay
   */
  @Test
  public void dateTime2JulDay() {
    assertEquals(firstDayOfGregorian, Companion.getGmtJulDay(LocalDate.of(1582, 10, 15), LocalTime.MIDNIGHT), 0.0);
    assertEquals(firstDayOfGregorian - 1, Companion.getGmtJulDay(JulianDate.of(1582, 10, 4), LocalTime.MIDNIGHT), 0.0);
  }


  /**
   * 確認四個日期的 toInstant() 相等
   */
  @Test
  public void testInstantEquals() {
    assertEquals(d1J.toInstant(ZoneOffset.UTC) , d1G.toInstant(ZoneOffset.UTC));
    assertEquals(d2J.toInstant(ZoneOffset.UTC) , d2G.toInstant(ZoneOffset.UTC));
    assertEquals(d3J.toInstant(ZoneOffset.UTC) , d3G.toInstant(ZoneOffset.UTC));
    assertEquals(d4J.toInstant(ZoneOffset.UTC) , d4G.toInstant(ZoneOffset.UTC));
  }

  /**
   * 確認四個日期轉換出來的 epoch second 相等
   */
  @Test
  public void testEpochSecondEquals() {
    assertEquals(GREGORIAN_START_INSTANT - 60 * 60 * 24 * 2, d1J.toInstant(ZoneOffset.UTC).getEpochSecond());
    assertEquals(GREGORIAN_START_INSTANT - 60 * 60 * 24    , d2J.toInstant(ZoneOffset.UTC).getEpochSecond());
    assertEquals(GREGORIAN_START_INSTANT                   , d3J.toInstant(ZoneOffset.UTC).getEpochSecond());
    assertEquals(GREGORIAN_START_INSTANT + 60 * 60 * 24    , d4J.toInstant(ZoneOffset.UTC).getEpochSecond());
  }

  /**
   * 確認 : 四組日期的 epoch day 相等
   */
  @Test
  public void testEpochDay() {
    assertEquals(d1J.toLocalDate().toEpochDay() , d1G.toLocalDate().toEpochDay());
    assertEquals(d2J.toLocalDate().toEpochDay() , d2G.toLocalDate().toEpochDay());
    assertEquals(d3J.toLocalDate().toEpochDay() , d3G.toLocalDate().toEpochDay());
    assertEquals(d4J.toLocalDate().toEpochDay() , d4G.toLocalDate().toEpochDay());

    // 比對四組日期的 epoch day
    assertEquals(-141429 , d1J.toLocalDate().toEpochDay());
    assertEquals(-141428 , d2J.toLocalDate().toEpochDay());
    assertEquals(-141427 , d3J.toLocalDate().toEpochDay()); // cutover 開始
    assertEquals(-141426 , d4J.toLocalDate().toEpochDay());
  }

  /**
   * 測試 {@link TimeTools#isBetween} , 不同曆法之間的日期可以正確判斷是否 between 兩日期之間
   */
  @Test
  public void testBetween() {
    // 同曆法 , 確認 : d2 位於 d1 與 d3 之間
    assertTrue(Companion.isBetween(d2J , d1J , d3J));
    assertTrue(Companion.isBetween(d2J , d3J , d1J));
    assertTrue(Companion.isBetween(d2G , d1G , d3G));
    assertTrue(Companion.isBetween(d2G , d3G , d1G));

    // 同曆法 , 確認 : d3 位於 d2 與 d4 之間
    assertTrue(Companion.isBetween(d3J , d2J , d4J));
    assertTrue(Companion.isBetween(d3J , d4J , d2J));
    assertTrue(Companion.isBetween(d3G , d2G , d4G));
    assertTrue(Companion.isBetween(d3G , d4G , d2G));

    // 跨曆法 , 確認 : d2 位於 d1 與 d3 之間
    assertTrue(Companion.isBetween(d2G , d1J , d3J));
    assertTrue(Companion.isBetween(d2G , d3J , d1J));
    assertTrue(Companion.isBetween(d2J , d1G , d3G));
    assertTrue(Companion.isBetween(d2J , d3G , d1G));

    // 跨曆法 , 確認 : d3 位於 d2 與 d4 之間
    assertTrue(Companion.isBetween(d3J , d2G , d4G));
    assertTrue(Companion.isBetween(d3J , d4G , d2G));
    assertTrue(Companion.isBetween(d3G , d2J , d4J));
    assertTrue(Companion.isBetween(d3G , d4J , d2J));
  }

  /** 測試 {@link TimeTools#isAfter(ChronoLocalDateTime, ChronoLocalDateTime)} 能否比對不同曆法之間的日期 */
  @Test
  public void testAfter() {
    // 同曆法 (J)
    assertTrue(Companion.isAfter(d2J , d1J));
    assertTrue(Companion.isAfter(d3J , d2J));
    assertTrue(Companion.isAfter(d4J , d3J));
    // 同曆法 (G)
    assertTrue(Companion.isAfter(d2G , d1G));
    assertTrue(Companion.isAfter(d3G , d2G));
    assertTrue(Companion.isAfter(d4G , d3G));

    // 不同曆法
    assertTrue(Companion.isAfter(d2G , d1J));
    assertTrue(Companion.isAfter(d3G , d2J));
    assertTrue(Companion.isAfter(d4G , d3J));

    assertTrue(Companion.isAfter(d2J , d1G));
    assertTrue(Companion.isAfter(d3J , d2G));
    assertTrue(Companion.isAfter(d4J , d3G));
  }

  /** 測試 {@link TimeTools#isBefore(ChronoLocalDateTime, ChronoLocalDateTime)} 能否比對不同曆法之間的日期 */
  @Test
  public void testBefore() {
    // 同曆法 (J)
    assertTrue(Companion.isBefore(d1J , d2J));
    assertTrue(Companion.isBefore(d2J , d3J));
    assertTrue(Companion.isBefore(d3J , d4J));
    // 同曆法 (G)
    assertTrue(Companion.isBefore(d1G , d2G));
    assertTrue(Companion.isBefore(d2G , d3G));
    assertTrue(Companion.isBefore(d3G , d4G));

    // 不同曆法
    assertTrue(Companion.isBefore(d1J , d2G));
    assertTrue(Companion.isBefore(d2J , d3G));
    assertTrue(Companion.isBefore(d3J , d4G));

    assertTrue(Companion.isBefore(d1G , d2J));
    assertTrue(Companion.isBefore(d2G , d3J));
    assertTrue(Companion.isBefore(d3G , d4J));
  }
}
