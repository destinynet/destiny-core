/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.threeten.extra.chrono.JulianDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class TimeToolsTest {

  // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
  double firstDayOfGregorian = 2299160.5;

  /**
   * 從「日期」、「時間」分開物件，轉換成 julDay
   */
  @Test
  public void dateTime2JulDay() throws Exception {
    assertEquals(firstDayOfGregorian, TimeTools.getGmtJulDay(LocalDate.of(1582, 10, 15), LocalTime.MIDNIGHT), 0.0);
    assertEquals(firstDayOfGregorian - 1, TimeTools.getGmtJulDay(JulianDate.of(1582, 10, 4), LocalTime.MIDNIGHT), 0.0);
  }

  /**
   * 從 「日期時間」整合物件，轉換成 julDay
   */
  @Test
  public void dateTime2JulDay2() throws Exception {
    // Gregorian 開始第一天
    // G : 半夜
    assertEquals(firstDayOfGregorian, TimeTools.getGmtJulDay(LocalDateTime.of(1582, 10, 15, 0, 0)), 0.0);
    // G : 中午
    assertEquals(firstDayOfGregorian + 0.5, TimeTools.getGmtJulDay(LocalDateTime.of(1582, 10, 15, 12, 0)), 0.0);

    // 前一天
    // J : 半夜
    assertEquals(firstDayOfGregorian - 1, TimeTools.getGmtJulDay(JulianDateTime.of(1582, 10, 4, 0, 0)), 0.0);
    // J : 中午
    assertEquals(firstDayOfGregorian - 1 + 0.5, TimeTools.getGmtJulDay(JulianDateTime.of(1582, 10, 4, 12, 0)), 0.0);

  }

  /**
   * epoch (1970-01-01 0:00) 為 2440587.5
   */
  @Test
  public void test1970Epoch() {
    assertEquals(2440587.5 , TimeTools.getGmtJulDay(LocalDateTime.of(1970 , 1, 1 , 0 , 0)) , 0.0);
  }

  /**
   * 現代 G/J 相差 13 天
   */
  @Test
  public void test_getJulDayFromGregorian() {
    assertEquals(2457774, TimeTools.getGmtJulDay(LocalDateTime.of(2017, 1, 20, 12, 0, 0)), 0.0);
    assertEquals(2457774, TimeTools.getGmtJulDay(JulianDateTime.of(2017, 1, 7, 12, 0, 0)), 0.0);
  }


  /**
   * https://docs.kde.org/trunk5/en/kdeedu/kstars/ai-julianday.html
   *
   * Julian day epoch 測試
   * 已知： epoch 位於
   * January   1, 4713 BC 中午 (proleptic year = -4712) :  proleptic Julian calendar
   * November 24, 4714 BC 中午 (proleptic year = -4713)  : proleptic Gregorian calendar
   */
  @Test
  public void testJulDayZero() {
    double startJulG = TimeTools.getGmtJulDay(LocalDateTime.of(-4713 , 11 , 24 , 12 , 0));
    assertEquals(0 , startJulG , 0.0);

    double startJulJ = TimeTools.getGmtJulDay(JulianDateTime.of(-4712 , 1 , 1 , 12 , 0));
    assertEquals(0 , startJulJ , 0.0);
  }

  @Test
  public void testJulianDateTime() {
    // 中午 , 0
    assertEquals(2457774, TimeTools.getGmtJulDay(JulianDateTime.of(2017, 1, 7, 12, 0)), 0.0);

    // 下午 6點，過了 0.25天
    assertEquals(2457774.25, TimeTools.getGmtJulDay(JulianDateTime.of(2017, 1, 7, 18, 0)), 0.0);

    // 晚上12點，過了 0.5天
    assertEquals(2457774.5, TimeTools.getGmtJulDay(JulianDateTime.of(2017, 1, 8, 0, 0)), 0.0);   // (g)1/21

    // 隔天早上 6點，過了 0.75天
    assertEquals(2457774.75, TimeTools.getGmtJulDay(JulianDateTime.of(2017, 1, 8, 6, 0)), 0.0);
  }
}