/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.chrono.JulianDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TimeToolsTest {

  // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
  double firstDayOfGregorian = 2299160.5;

  private Logger logger = LoggerFactory.getLogger(getClass());

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

  @Test
  public void getGmtFromLmt() {
    LocalDateTime now = LocalDateTime.now();
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(now , ZoneId.systemDefault());
    logger.info("now = {} , gmt = {}" , now , gmt);
  }

  /**
   * 已知：
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  public void testGetGmtFromLmt_1974_DST() {
    ChronoLocalDateTime lmt;
    ZoneId zoneId = ZoneId.of("Asia/Taipei");

    // 日光節約時間前一秒
    lmt = LocalDateTime.of(1974, 3, 31, 23, 59, 59);
    // 相差八小時
    assertEquals(LocalDateTime.of(1974, 3, 31, 15, 59, 59), TimeTools.getGmtFromLmt(lmt, zoneId));

    //加上一秒
    lmt = LocalDateTime.of(1974, 4, 1, 0, 0, 0);
    assertEquals(LocalDateTime.of(1974, 3, 31, 16, 0, 0), TimeTools.getGmtFromLmt(lmt, zoneId));

    //真正日光節約時間，開始於 1:00AM , 時差變為 GMT+9
    lmt = LocalDateTime.of(1974, 4, 1, 1, 0, 0);
    assertEquals(LocalDateTime.of(1974, 3, 31, 16, 0, 0), TimeTools.getGmtFromLmt(lmt, zoneId)); //真正銜接到「日光節約時間」前一秒

    // 日光節約時間 , 結束前一秒 , 仍是 GMT+9
    lmt = LocalDateTime.of(1974, 9, 30, 23, 59, 59);
    assertEquals(LocalDateTime.of(1974, 9, 30, 14, 59, 59), TimeTools.getGmtFromLmt(lmt, zoneId));

    lmt = TimeTools.getLmtFromGmt(LocalDateTime.of(1974, 9, 30, 14, 0, 0), Location.of(Locale.TAIWAN));
    System.err.println(lmt); //推估當時可能過了兩次 23:00-24:00 的時間，以調節和 GMT 的時差

    // 結束日光節約時間 , 調回 GMT+8
    lmt = LocalDateTime.of(1974, 10, 1, 0, 0, 0);
    assertEquals(LocalDateTime.of(1974, 9, 30, 16, 0, 0), TimeTools.getGmtFromLmt(lmt, zoneId));
  }

  /**
   * 已知：
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  public void testGetLmtFromGmt() {
    Location loc = new Location(121.30, 25.03, TimeZone.getTimeZone("Asia/Taipei"));
    ChronoLocalDateTime gmt, lmt;

    //日光節約時間前一秒
    gmt = LocalDateTime.of(1974, 3, 31, 15, 59, 0);
    lmt = TimeTools.getLmtFromGmt(gmt, loc);
    logger.info("日光節約前一秒  : {}" , lmt);
    assertEquals(LocalDateTime.of(1974, 3, 31, 23, 59, 0), lmt);

    //開始日光節約時間
    gmt = LocalDateTime.of(1974, 3, 31, 16, 0, 0);
    lmt = TimeTools.getLmtFromGmt(gmt, loc);
    logger.info("開始日光節約時間 : {} , 與之前跳躍一小時" , lmt);
    assertEquals(LocalDateTime.of(1974, 4, 1, 1, 0, 0), lmt); //跳躍一小時

    //日光節約時間結束前一秒
    gmt = LocalDateTime.of(1974, 9, 30, 14, 59, 59);
    lmt = TimeTools.getLmtFromGmt(gmt, loc);
    logger.info("結束日光節約時間前一秒 : {}" , lmt);
    assertEquals(LocalDateTime.of(1974, 9, 30, 23, 59, 59), lmt); //與下面重複

    //日光節約時間結束前一秒
    gmt = LocalDateTime.of(1974, 9, 30, 15, 59, 59);
    ChronoLocalDateTime lmt2 = TimeTools.getLmtFromGmt(gmt, loc);
    assertEquals(lmt , lmt2); // GMT 過了一小時，但轉換出來的 LMT 都相等 , 這部分會重疊
    assertEquals(LocalDateTime.of(1974, 9, 30, 23, 59, 59), lmt); //與上面重複

    //日光節約時間結束
    gmt = LocalDateTime.of(1974, 9, 30, 16, 0, 0);
    lmt = TimeTools.getLmtFromGmt(gmt, loc);
    logger.info("真正結束日光節約時間   : {}" , lmt);
    assertEquals(LocalDateTime.of(1974, 10, 1, 0, 0, 0), lmt);
  }
}