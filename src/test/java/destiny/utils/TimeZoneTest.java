/** 2009/10/20 下午10:53:51 by smallufo */
package destiny.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.zone.ZoneRulesException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TimeZoneTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testTimeZone() {
    TimeZone tp = TimeZone.getTimeZone("Asia/Taipei");
    TimeZone sh = TimeZone.getTimeZone("Asia/Shanghai");

    assertNotEquals(tp, sh);
  }

  /**
   * 列出東八區 的 tz
   */
  @Test
  public void testGetAvailableIds() {
    for (String tz : TimeZone.getAvailableIDs(8 * 60 * 60 * 1000)) {
      TimeZone timezone = TimeZone.getTimeZone(tz);
      ZoneId zoneId = timezone.toZoneId();
      logger.info("tz = {} , \tzoneId = {} , \tdisplayName(ENG) = {} , \tdisplayName(TW) = {}" ,
        timezone.getDisplayName(false , TimeZone.SHORT , Locale.ENGLISH) ,
        zoneId ,
        zoneId.getDisplayName(TextStyle.FULL , Locale.ENGLISH),
        zoneId.getDisplayName(TextStyle.FULL , Locale.TAIWAN)
      );
    }
  }

  /**
   * zoneId count = 595
   *
   * ZoneID 可以完全轉換到 TimeZone
   */
  @Test
  public void test_ZoneId_to_TimeZone() {
    logger.info("zoneId count = {}" , ZoneId.getAvailableZoneIds().size());

    for (String id : ZoneId.getAvailableZoneIds()) {
      TimeZone tz = TimeZone.getTimeZone(id);
      logger.info("id = {} , \ttz.displayName = {}" , id , tz.getDisplayName(Locale.TAIWAN));
    }
  }

  /**
   * TimeZone count = 623
   * 但 ZoneID 只有 595 個
   * 有許多 TimeZone 無法轉換到 ZoneID
   * 這些「都是」 3-bytes 的 TimeZoneID
   * 例如： CTT、 PST、 EST 等許多 3-bytes 的 TimeZoneID
   * 因為這些並非 unique , 見 {@link ZoneId#of(String, Map)} 說明
   *
   * 但「並非所有 3-bytes」的 id 都無法轉到 ZoneID , 許多仍可轉換，例如 GMT , UTC 仍可用
   * 參照 http://stackoverflow.com/a/41683097/298430
   */
  @Test
  public void test_TimeZone_to_ZoneId() {
    logger.info("TimeZone count = {}" , TimeZone.getAvailableIDs().length);
    for(String id : TimeZone.getAvailableIDs()) {
      try {
        ZoneId zoneId = ZoneId.of(id);
        logger.info("id = {} , zoneId = {}" , id , zoneId);
      } catch (ZoneRulesException e) {
        logger.warn("Cannot get ZoneId from {}" , id);
        if (id.length() != 3) {
          logger.error("{} 長度不為 3 !" , id);
        }
      }
    }
  }

  /**
   * 因為 ZoneId.of(PST) 西岸（太平洋）時區， 無此值
   * 想得知 TimeZone(PST) 到 ZoneId 會變成 洛杉磯時區
   *
   * ZoneId.of(EST) 東岸時區， 無此值
   * TimeZone(EST) 到 ZoneId 會變成 "-5:00"
   *
   * 這些轉換，定義在 {@link ZoneId#SHORT_IDS} 裡面
   */
  @Test
  public void test_TimeZone_to_ZoneId_incompatibilities() {
    assertEquals("America/Los_Angeles" , TimeZone.getTimeZone("PST").toZoneId().toString());

    assertEquals("-05:00" , TimeZone.getTimeZone("EST").toZoneId().toString());
  }



  /**
   * 東八區的 tz , display name
   */
  @Test
  public void testZoneIdDisplayName() {
    for(String id : TimeZone.getAvailableIDs(8 * 60 * 60 * 1000)) {
      try {
        ZoneId zoneId = ZoneId.of(id);
        logger.info("id = {} , \t\tzoneId = {}" , id , zoneId);
        assertEquals(id , zoneId.toString());
      } catch (ZoneRulesException e) {
        logger.error("無法從 {} 找到 ZoneId : {}" , id , e.getMessage());
      }
    }
  }

  @Test
  public void testSingapore() {
    TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
    System.out.println(tz);

    GregorianCalendar cal;

    cal = new GregorianCalendar(1981, 12 - 1, 31, 23, 00);
    System.out.println("(1) 1981/12/31  23:00 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    cal = new GregorianCalendar(1981, 12 - 1, 31, 23, 59);
    System.out.println("(2) 1981/12/31  23:59 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    cal = new GregorianCalendar(1982, 1 - 1, 1, 0, 0);
    System.out.println("(3) 1982/01/01  00:00 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    cal = new GregorianCalendar(1982, 1 - 1, 1, 0, 29, 59);
    System.out.println("(4) 1982/01/01  00:29 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    cal = new GregorianCalendar(1982, 1 - 1, 1, 0, 30, 0);
    System.out.println("(5) 1982/01/01  00:30 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());


    cal = new GregorianCalendar();
    cal.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
    cal.setTimeInMillis(378664200000l);
    System.out.println(cal.getTime());

    cal.setTimeInMillis(378664200000l - 1000);
    System.out.println(cal.getTime());


  }

  @Test
  public void testTaiwan() {
    TimeZone tp = TimeZone.getTimeZone("Asia/Taipei");
    System.out.println(tp);

    GregorianCalendar cal;

    cal = new GregorianCalendar(1945, 4 - 1, 30, 23, 59);
    System.out.println("(1) 1945/04/30  23:59 : DST ? " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    //根據資料：台灣於 1945/5/1 凌晨 0 時進入 DST , 撥快一小時
    cal = new GregorianCalendar(1945, 5 - 1, 1, 0, 0);
    System.out.println("(2) 1945/05/01  00:00 : DST ? " + tp.inDaylightTime(cal.getTime()) + " , " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    cal = new GregorianCalendar(1945, 5 - 1, 1, 0, 1);
    System.out.println("(3) 1945/05/01  00:01 : DST ? " + tp.inDaylightTime(cal.getTime()) + " , " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    //根據資料：台灣於 1945/10/1 凌晨 0 時離開 DST , 結束日光節約時間
    cal = new GregorianCalendar(1945, 9 - 1, 30, 22, 59);
    System.out.println("(4) 1945/09/30  22:59 : DST ? " + tp.inDaylightTime(cal.getTime()) + " , " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    //注意，這裡要小心！有點詭異。為什麼晚上 23 點就結束 DST ??

    cal = new GregorianCalendar(1945, 9 - 1, 30, 23, 00);
    System.out.println("(5) 1945/09/30  23:00 : DST ? " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    cal = new GregorianCalendar(1945, 10 - 1, 1, 0, 0);
    System.out.println("(6) 1945/10/01  00:00 : DST ? " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
  }

  /**
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  public void testTaiwan63() {
    TimeZone tp = TimeZone.getTimeZone("Asia/Taipei");
    System.out.println(tp);
    TimeZone gmt = TimeZone.getTimeZone("GMT");

    GregorianCalendar cal;


    cal = new GregorianCalendar(gmt, Locale.UK);


    System.out.println(cal);
    System.out.println(gmt.getOffset(Calendar.ZONE_OFFSET));
    System.out.println(tp.getOffset(Calendar.ZONE_OFFSET));

    System.out.println("日光節約時間前");
    cal.set(1974, 3 - 1, 31, 15, 59, 59);
    System.out.println(cal.getTime() + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    System.out.println("開始日光節約時間");
    cal.set(1974, 3 - 1, 31, 16, 0, 0);
    System.out.println("hour = " + cal.get(Calendar.HOUR_OF_DAY));
    System.out.println(cal + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    //日光節約時間中
    cal.set(1974, 3 - 1, 31, 16, 1, 0);
    System.out.println(cal.getTime() + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    cal.set(1974, 3 - 1, 31, 16, 59, 0);
    System.out.println(cal.getTime() + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset() + ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
  }

}

