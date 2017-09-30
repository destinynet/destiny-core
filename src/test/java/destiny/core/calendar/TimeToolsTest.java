/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.chrono.JulianDate;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static destiny.core.calendar.TimeTools.getDstSecondOffset;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    assertEquals(2440587.5, TimeTools.getGmtJulDay(LocalDateTime.of(1970, 1, 1, 0, 0)), 0.0);
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
   * <p>
   * Julian day epoch 測試
   * 已知： epoch 位於
   * January   1, 4713 BC 中午 (proleptic year = -4712) :  proleptic Julian calendar
   * November 24, 4714 BC 中午 (proleptic year = -4713)  : proleptic Gregorian calendar
   */
  @Test
  public void testJulDayZero() {
    double startJulG = TimeTools.getGmtJulDay(LocalDateTime.of(-4713, 11, 24, 12, 0));
    assertEquals(0, startJulG, 0.0);

    double startJulJ = TimeTools.getGmtJulDay(JulianDateTime.of(-4712, 1, 1, 12, 0));
    assertEquals(0, startJulJ, 0.0);
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
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(now, ZoneId.systemDefault());
    logger.info("now = {} , gmt = {}", now, gmt);
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
    logger.info("日光節約前一秒  : {}", lmt);
    assertEquals(LocalDateTime.of(1974, 3, 31, 23, 59, 0), lmt);

    //開始日光節約時間
    gmt = LocalDateTime.of(1974, 3, 31, 16, 0, 0);
    lmt = TimeTools.getLmtFromGmt(gmt, loc);
    logger.info("開始日光節約時間 : {} , 與之前跳躍一小時", lmt);
    assertEquals(LocalDateTime.of(1974, 4, 1, 1, 0, 0), lmt); //跳躍一小時

    //日光節約時間結束前一秒
    gmt = LocalDateTime.of(1974, 9, 30, 14, 59, 59);
    lmt = TimeTools.getLmtFromGmt(gmt, loc);
    logger.info("結束日光節約時間前一秒 : {}", lmt);
    assertEquals(LocalDateTime.of(1974, 9, 30, 23, 59, 59), lmt); //與下面重複

    //日光節約時間結束前一秒
    gmt = LocalDateTime.of(1974, 9, 30, 15, 59, 59);
    ChronoLocalDateTime lmt2 = TimeTools.getLmtFromGmt(gmt, loc);
    assertEquals(lmt, lmt2); // GMT 過了一小時，但轉換出來的 LMT 都相等 , 這部分會重疊
    assertEquals(LocalDateTime.of(1974, 9, 30, 23, 59, 59), lmt); //與上面重複

    //日光節約時間結束
    gmt = LocalDateTime.of(1974, 9, 30, 16, 0, 0);
    lmt = TimeTools.getLmtFromGmt(gmt, loc);
    logger.info("真正結束日光節約時間   : {}", lmt);
    assertEquals(LocalDateTime.of(1974, 10, 1, 0, 0, 0), lmt);
  }

  /**
   * 台灣
   * https://blog.yorkxin.org/2014/07/11/dst-in-taiwan-study
   */
  @Test
  public void getDstSecondOffset_Taiwan() throws Exception {
    Location loc = Location.of(Locale.TAIWAN);

    // 民國41年（西元1952年）	日光節約時間	3月1日至10月31日
    int year = 1952;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 2, 28, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 31, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 11, 1, 12, 0), loc));

    //民國42年至43年（西元1953-1954年）	日光節約時間	4月1日至10月31日
    year = 1953;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 31, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 11, 1, 12, 0), loc));

    year = 1954;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 31, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 11, 1, 12, 0), loc));

    //民國44年至45年（西元1955-1956年）	日光節約時間	4月1日至9月30日
    year = 1955;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));

    year = 1956;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));

    // 民國46年至48年（西元1957-1959年）	夏令時間	4月1日至9月30日
    year = 1957;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));

    year = 1958;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));

    year = 1959;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));


    // 民國49年至50年（西元1960-1961年）	夏令時間	6月1日至9月30日
    year = 1960;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 5, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 6, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));

    year = 1961;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 5, 31, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 6, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));


    // 民國63年至64年（西元1974-1975年）	日光節約時間	4月1日至9月30日
    year = 1974;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 30, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));

    year = 1975;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 3, 30, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 4, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));

    // 民國68年（西元1979年）	日光節約時間	7月1日至9月30日
    year = 1979;
    // 日光節約時間，前一天中午 , GMT+8
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 6, 30, 12, 0), loc));
    // 日光節約時間開始，當天中午 , 時區調快一小時
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 7, 1, 12, 0), loc));
    // 日光節約時間結束當天中午，時區仍是 +9
    assertEquals(tuple(TRUE, 9 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 9, 30, 12, 0), loc));
    // 日光節約時間結束 , 隔天中午，時區回到 +9
    assertEquals(tuple(FALSE, 8 * 60 * 60), getDstSecondOffset(LocalDateTime.of(year, 10, 1, 12, 0), loc));
  }


  /**
   * 已攜帶 Zone 的 日期＋時間，轉換成 GMT 的日期＋時間
   */
  @Test
  public void getGmtFromZonedDateTime() {
    // 2017-09-30 , 14:00 , 台灣
    LocalDateTime lmt = LocalDateTime.of(2017, 9, 30, 14, 0);
    ChronoZonedDateTime zdt = TimeTools.getGmtFromZonedDateTime(lmt.atZone(ZoneId.of("Asia/Taipei")));
    // 確認傳回的是 GMT 時區
    assertEquals(ZoneId.of("GMT"), zdt.getZone());
    // GMT 為清晨 6點
    assertEquals(LocalDateTime.of(2017, 9, 30, 6, 0), zdt.toLocalDateTime());
    assertEquals(2458026.75, TimeTools.getJulDay(zdt), 0);
  }

  /**
   * {@link LocalDateTime} 附加上 {@link ZoneOffset} 的話，轉換到台北時間 , 產生一個 {@link ZonedDateTime}
   */
  @Test
  public void test_ZonedOffset_to_ZonedDateTime_Taiwan() {
    ZoneId asiaTaipei = ZoneId.of("Asia/Taipei");

    // 2017-09-30 12:00中午
    LocalDateTime lmt = LocalDateTime.of(2017,9,30,12,0);
    logger.info("若此時間位於 UTC : {}" , OffsetDateTime.of(lmt, ZoneOffset.UTC));

    ZonedDateTime zdt = OffsetDateTime.of(lmt, ZoneOffset.ofHours(1)).atZoneSameInstant(asiaTaipei);
    logger.info("若此時間位於 UTC+1h , 則台北時間應該+7小時 : {}" , zdt);
    assertEquals(lmt.plusHours(7) , zdt.toLocalDateTime());


    zdt = OffsetDateTime.of(lmt, ZoneOffset.ofHours(2)).atZoneSameInstant(asiaTaipei);
    logger.info("若此時間位於 UTC+2h , 則台北時間應該+6小時 : {}" , zdt);
    assertEquals(lmt.plusHours(6) , zdt.toLocalDateTime());


    // 1975-04-01 到 1975-10-01 , Asia/Taipei 施行日光節約時間
    lmt = LocalDateTime.of(1975,7,1,12,0);  // 先取 GMT , 七月一日，中午
    assertEquals(LocalDateTime.of(1975,7,1,12,0), OffsetDateTime.of(lmt , ZoneOffset.UTC).toLocalDateTime()); // 轉到 GMT , 再取 LMT , 應該相等

    zdt = OffsetDateTime.of(lmt , ZoneOffset.UTC).atZoneSameInstant(asiaTaipei);
    logger.info("若此時間位於 UTC , 則台北時間 , 本來應該+8小時 , 但台北撥快一小時，所以應該要 +9h: {}" , zdt);
    assertEquals(lmt.plusHours(9) , zdt.toLocalDateTime());

    zdt = OffsetDateTime.of(lmt , ZoneOffset.ofHours(1)).atZoneSameInstant(asiaTaipei);
    logger.info("若此時間位於 UTC+1h , 則台北時間 , 本來應該+7小時 , 但台北撥快一小時，所以應該要 +8h: {}" , zdt);
    assertEquals(lmt.plusHours(8) , zdt.toLocalDateTime());


  }

  /**
   * 1582 切換點 (jd = 2299160.5) ，的 Zoned 時間，轉為 Julian Day
   */
  @Test
  public void getFromZonedDateTime_1582() {
    // 1582-10-15 , 8:00 , 台灣
    LocalDateTime lmt = LocalDateTime.of(1582, 10, 15, 8, 0);

    logger.info("offsetDateTime = {}" , OffsetDateTime.of(lmt, ZoneOffset.UTC));
    logger.info("offsetDateTime = {}" , OffsetDateTime.of(lmt, ZoneOffset.UTC).atZoneSameInstant(ZoneId.of("Asia/Taipei")));

    // 上海 was at UTC+08:05:43 , 比 GMT 快了 8h , 5m , 43s
    logger.info("offsetDateTime = {}" , OffsetDateTime.of(lmt, ZoneOffset.UTC).atZoneSameInstant(ZoneId.of("Asia/Shanghai")));
    // 香港 was at UTC+07:36:42
    logger.info("offsetDateTime = {}" , OffsetDateTime.of(lmt, ZoneOffset.UTC).atZoneSameInstant(ZoneId.of("Asia/Hong_Kong")));

    ChronoZonedDateTime zdt = TimeTools.getGmtFromZonedDateTime(lmt.atOffset(ZoneOffset.ofHours(8)).atZoneSameInstant(ZoneId.of("Asia/Taipei")));
    // 確認傳回的是 GMT 時區
    assertEquals(ZoneId.of("GMT"), zdt.getZone());
    // GMT 為清晨 0點
    assertEquals(LocalDateTime.of(1582, 10, 15, 0, 0), zdt.toLocalDateTime());
    assertEquals(2299160.5, TimeTools.getJulDay(zdt), 0);


    // 1582-10-15 , 9:00 , 日本
    lmt = LocalDateTime.of(1582, 10, 15, 9, 0);
    zdt = TimeTools.getGmtFromZonedDateTime(lmt.atOffset(ZoneOffset.ofHours(9)).atZoneSameInstant(ZoneId.of("Asia/Tokyo")));
    // 確認傳回的是 GMT 時區
    assertEquals(ZoneId.of("GMT"), zdt.getZone());
    // GMT 為清晨 0點
    logger.info("gmt = {}", zdt);
    assertEquals(LocalDateTime.of(1582, 10, 15, 0, 0), zdt.toLocalDateTime());
    assertEquals(2299160.5, TimeTools.getJulDay(zdt), 0);
  }



  @Test
  public void testBetween() {
    LocalDateTime t1 = LocalDateTime.of(2017,3,6,19,0,1);
    LocalDateTime t  = LocalDateTime.of(2017,3,6,19,0,2);
    LocalDateTime t2 = LocalDateTime.of(2017,3,6,19,0,3);

    assertTrue(TimeTools.isBetween(t , t1 , t2));
    assertTrue(TimeTools.isBetween(t , t2 , t1));
    assertFalse(TimeTools.isBetween(t1 , t , t2));
    assertFalse(TimeTools.isBetween(t1 , t2 , t1));
    assertFalse(TimeTools.isBetween(t2 , t , t1));
    assertFalse(TimeTools.isBetween(t2 , t1 , t));

    t1 = LocalDateTime.of(-2017,3,6,19,0,1);
    t  = LocalDateTime.of(-2017,3,6,19,0,2);
    t2 = LocalDateTime.of(-2017,3,6,19,0,3);

    assertTrue(TimeTools.isBetween(t , t1 , t2));
    assertTrue(TimeTools.isBetween(t , t2 , t1));
    assertFalse(TimeTools.isBetween(t1 , t , t2));
    assertFalse(TimeTools.isBetween(t1 , t2 , t1));
    assertFalse(TimeTools.isBetween(t2 , t , t1));
    assertFalse(TimeTools.isBetween(t2 , t1 , t));
  }
}