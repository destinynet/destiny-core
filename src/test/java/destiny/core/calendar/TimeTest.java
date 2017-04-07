/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 01:47:36
 */
package destiny.core.calendar;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.chrono.JulianDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Locale;
import java.util.TimeZone;

import static destiny.core.calendar.Time.getDstSecondOffset;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.Assert.*;

public class TimeTest
{
  private Logger logger = LoggerFactory.getLogger(getClass());

  private Time time;
  private Time origin;
  private Time actual ;
  private Time expected ;


  /**
   * 已知：
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  public void testGetGmtFromLmt() {
    Location loc = new Location(121.30, 25.03, TimeZone.getTimeZone("Asia/Taipei"));
    LocalDateTime gmt, lmt;

    //日光節約時間前一秒
    lmt = LocalDateTime.of(1974, 3, 31, 23, 59, 59);
    gmt = Time.getGmtFromLmt(lmt, loc);
    assertEquals(LocalDateTime.of(1974, 3, 31, 15, 59, 59), gmt);

    //加上一秒，開始日光節約時間，時間調快一小時 , 變成 GMT+9
    lmt = LocalDateTime.of(1974, 4, 1, 0, 0, 0);
    gmt = Time.getGmtFromLmt(lmt, loc);
    assertEquals(LocalDateTime.of(1974, 3, 31, 16, 0, 0), gmt);

    //真正日光節約時間，開始於 1:00AM
    lmt = LocalDateTime.of(1974, 4, 1, 1, 0, 0);
    gmt = Time.getGmtFromLmt(lmt, loc);
    assertEquals(LocalDateTime.of(1974, 3, 31, 16, 0, 0), gmt); //真正銜接到「日光節約時間」前一秒

    // 日光節約時間前一秒 , 仍是 GMT+9
    lmt = LocalDateTime.of(1974, 9, 30, 23, 59, 59);
    gmt = Time.getGmtFromLmt(lmt, loc);
    assertEquals(LocalDateTime.of(1974, 9, 30, 14, 59, 59), gmt);

    lmt = Time.getLmtFromGmt(LocalDateTime.of(1974, 9, 30, 14, 0, 0), loc);
    System.err.println(lmt); //推估當時可能過了兩次 23:00-24:00 的時間，以調節和 GMT 的時差


    // 結束日光節約時間 , 調回 GMT+8
    lmt = LocalDateTime.of(1974, 10, 1, 0, 0, 0);
    gmt = Time.getGmtFromLmt(lmt, loc);
    assertEquals(LocalDateTime.of(1974, 9, 30, 16, 0, 0), gmt);
  }

  /**
   * 已知：
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  public void testGetLmtFromGmt()
  {
    Location loc = new Location(121.30, 25.03, TimeZone.getTimeZone("Asia/Taipei"));
    LocalDateTime gmt, lmt;

    //日光節約時間前一秒
    gmt = LocalDateTime.of(1974, 3, 31, 15, 59, 0);
    lmt = Time.getLmtFromGmt(gmt, loc);
    System.out.println(lmt);
    assertEquals(LocalDateTime.of(1974, 3, 31, 23, 59, 0), lmt);

    //開始日光節約時間
    gmt = LocalDateTime.of(1974, 3, 31, 16, 0, 0);
    lmt = Time.getLmtFromGmt(gmt, loc);
    System.out.println(lmt);
    assertEquals(LocalDateTime.of(1974, 4, 1, 1, 0, 0), lmt); //跳躍一小時

    //日光節約時間結束前一秒
    gmt = LocalDateTime.of(1974, 9, 30, 14, 59, 59);
    lmt = Time.getLmtFromGmt(gmt, loc);
    System.out.println(lmt);
    assertEquals(LocalDateTime.of(1974, 9, 30, 23, 59, 59), lmt); //與下面重複

    //日光節約時間結束前一秒
    gmt = LocalDateTime.of(1974, 9, 30, 15, 59, 59);
    lmt = Time.getLmtFromGmt(gmt, loc);
    System.out.println(lmt);
    assertEquals(LocalDateTime.of(1974, 9, 30, 23, 59, 59), lmt); //與上面重複

    //日光節約時間結束
    gmt = LocalDateTime.of(1974, 9, 30, 16, 0, 0);
    lmt = Time.getLmtFromGmt(gmt, loc);
    System.out.println(lmt);
    assertEquals(LocalDateTime.of(1974, 10, 1, 0, 0, 0), lmt);
  }




  /**
   * 從 julDay 傳回 LocalDate or JulianDate
   * 可以藉由這裡比對 http://aa.usno.navy.mil/data/docs/JulianDate.php
   */
  @Test
  public void testFromGmtJulDay() {
    // 測試 1582/10/4 --- 1582/10/15 的日期轉換

    // Gregorian 第一天 : 1582-10-15
    assertEquals(LocalDate.of(1582, 10, 15), Time.from(2299160.5).getLeft());
    Pair<ChronoLocalDate , LocalTime> pair = Time.from(2299160.75); // 加上 0.25 天
    assertEquals(LocalTime.of(6,0,0) , pair.getRight());            // 應該是早上六點
    pair = Time.from(2299161.0);                                    // 加上 0.5 天
    assertEquals(LocalTime.of(12,0,0) , pair.getRight());           // 應該是中午12點
    pair = Time.from(2299161.25);                                   // 加上 0.75 天
    assertEquals(LocalTime.of(18,0,0) , pair.getRight());           // 應該是晚上六點

    // JD 往前一天 , 跳到 1582/10/4 (J)
    assertEquals(JulianDate.of(1582, 10, 4), Time.from(2299159.5).getLeft());

    //西元元年一月一號 (J)
    assertEquals(JulianDate.of(1,1,1), Time.from(1721423.5).getLeft());

    //西元前一年十二月三十一號 (J)
    assertEquals(JulianDate.of(0,12,31), Time.from(1721422.5).getLeft());

    //西元前一年一月一號 (J)
    assertEquals(JulianDate.of(0,1,1), Time.from(1721057.5).getLeft());

    //西元前二年十二月三十一號 (J)
    assertEquals(JulianDate.of(-1,12,31), Time.from(1721056.5).getLeft());
  }



  /**
   * https://docs.kde.org/trunk5/en/kdeedu/kstars/ai-julianday.html
   *
   * Julian day epoch 測試
   * 已知： epoch 位於
   * January 1, 4713 BC 中午 :  proleptic Julian calendar
   * November 24, 4714 BC    : proleptic Gregorian calendar
   */
  @Test
  public void testJulDayZero() {
    double startJul = Time.getGmtJulDay(false , false , 4713, 1 , 1 , 12 , 0 , 0);
    assertEquals(0 , startJul , 0.0);

    double startJulVer2 = Time.getGmtJulDay(JulianDateTime.of(-4712 , 1 , 1 , 12 , 0));
    assertEquals(0 , startJulVer2 , 0.0);

    double startGre = Time.getGmtJulDay(false , true , 4714, 11 , 24 , 12 , 0 , 0);
    assertEquals(0 , startGre , 0.0);
  }

  /**
   * 已知
   * astro julian day number 開始於
   * November 24, 4714 BC 當天中午  : proleptic Gregorian calendar
   *
   * epoch (1970-01-01 0:00) 為 2440587.5
   */
  @Test
  public void test1970Epoch() {
    double value = Time.getGmtJulDay(true , true , 1970 , 1 , 1 , 0, 0, 0);
    assertEquals(2440587.5 , value , 0.0);

    value = Time.getGmtJulDay(LocalDateTime.of(1970 , 1 , 1 , 0 , 0 , 0));
    assertEquals(2440587.5 , value , 0.0);
  }

  /**
   * proleptic Gregorian
   */
  @Test
  public void test_getJulDayFromGregorian() {
    assertEquals(2457774, Time.getGmtJulDay(LocalDateTime.of(2017, 1, 20, 12, 0, 0)), 0.0);
    assertEquals(2457774, Time.getGmtJulDay(JulianDateTime.of(2017, 1, 7, 12, 0, 0)), 0.0);
  }


  
  /**
   * 台灣
   * https://blog.yorkxin.org/2014/07/11/dst-in-taiwan-study
   */
  @Test
  public void getDstSecondOffset_Taiwan() throws Exception {
    Location loc = new Location(Locale.TAIWAN);

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

  @Test
  public void testBetween() {
    LocalDateTime t1 = LocalDateTime.of(2017,3,6,19,0,1);
    LocalDateTime t  = LocalDateTime.of(2017,3,6,19,0,2);
    LocalDateTime t2 = LocalDateTime.of(2017,3,6,19,0,3);

    assertTrue(Time.isBetween(t , t1 , t2));
    assertTrue(Time.isBetween(t , t2 , t1));
    assertFalse(Time.isBetween(t1 , t , t2));
    assertFalse(Time.isBetween(t1 , t2 , t1));
    assertFalse(Time.isBetween(t2 , t , t1));
    assertFalse(Time.isBetween(t2 , t1 , t));

    t1 = LocalDateTime.of(-2017,3,6,19,0,1);
    t  = LocalDateTime.of(-2017,3,6,19,0,2);
    t2 = LocalDateTime.of(-2017,3,6,19,0,3);

    assertTrue(Time.isBetween(t , t1 , t2));
    assertTrue(Time.isBetween(t , t2 , t1));
    assertFalse(Time.isBetween(t1 , t , t2));
    assertFalse(Time.isBetween(t1 , t2 , t1));
    assertFalse(Time.isBetween(t2 , t , t1));
    assertFalse(Time.isBetween(t2 , t1 , t));
  }
}
