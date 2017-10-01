/**
 * Created by smallufo on 2017-09-29.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Locale;
import java.util.function.Function;

import static java.time.temporal.JulianFields.JULIAN_DAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InstantTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 西元 1582-10-15 0:0 的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
   */
  private final static long GREGORIAN_START_INSTANT = -12219292800L;

  /**
   * 日本時間比台灣快一小時
   * 因此，雖然 LDT 不一樣，但是轉換成 Instant 之後，卻都是相同的
   */
  @Test
  public void testInstant() {
    LocalDateTime ldt1 = LocalDateTime.of(2017 , 9 , 28 , 12 , 0);
    Instant instant1 = ldt1.atZone(ZoneId.of("Asia/Taipei")).toInstant();
    logger.info("instant from Taiwan : {}" , instant1);

    LocalDateTime ldt2 = LocalDateTime.of(2017 , 9 , 28 , 13 , 0);
    Instant instant2 = ldt2.atZone(ZoneId.of("Asia/Tokyo")).toInstant();
    logger.info("instant from Japan : {}" , instant2);

    assertEquals(instant1 , instant2);

    LocalDateTime ldt3 = LocalDateTime.of(1974 , 7 , 1 , 12 , 0);
    Instant instant3 = ldt3.atZone(ZoneId.of("Asia/Taipei")).toInstant();
    logger.info("台灣施行日光節約時間 : {}" , instant3);

    LocalDateTime ldt4 = LocalDateTime.of(1974 , 7 , 1 , 12 , 0);
    Instant instant4 = ldt4.atZone(ZoneId.of("Asia/Tokyo")).toInstant();
    logger.info("此時就與日本時間同步 : {}" , instant4);

    assertEquals(instant3 , instant4);
  }

  /**
   * 測試 Instant 是否能正確跨越 1582-10-04 到 1582-10-15
   */
  @Test
  public void testInstantEpochSecond() {
    LocalDateTime firstDay = LocalDateTime.of(1582,10,15 , 0 , 0);
    Instant instant = firstDay.atZone(ZoneId.of("GMT")).toInstant();
    logger.info("1582-10-15 instant = {} , 秒數 = {}" , instant , instant.getEpochSecond());

    // 減去一秒
    Instant instant2 = instant.minusSeconds(1);
    logger.info("減去一秒 , instant2 = {} " , instant2);


    logger.info("Greg 前一秒 : {}" , TimeSecDecorator.getOutputString(instant2.atZone(ZoneId.of("GMT")).toLocalDateTime() , Locale.TAIWAN));

    assertEquals(LocalDateTime.of(1582,10,14,23,59,59).toEpochSecond(ZoneOffset.UTC) , instant2.getEpochSecond());
  }

  /**
   * Instant 取得 julian day
   */
  @Test
  public void testInstantToJulianDay() {


    // 1582-10-15 開始 Gregorian Calendar , 真正 julDay = 2299160.5
    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 , 變成 2299161 . 因此最後需要減去 0.5

    long REAL_JUL_DAY_PLUS_HALF = 2299161;

    // 當天午夜
    Instant instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT);
    long halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY);
    assertEquals(REAL_JUL_DAY_PLUS_HALF , halfAddedJulDay);
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 , TimeTools.getJulDay(instant) , 0.0);        // jul day + 0

    // 當天 6:00
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT +  60 * 60 * 6);
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY);
    assertEquals(REAL_JUL_DAY_PLUS_HALF , halfAddedJulDay);
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 0.25, TimeTools.getJulDay(instant) , 0.0);  // jul day + 0.25

    // 當天中午 12:00
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT +  60 * 60 * 12);
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY);
    assertEquals(REAL_JUL_DAY_PLUS_HALF , halfAddedJulDay);
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 0.5, TimeTools.getJulDay(instant) , 0.0);   // jul day + 0.5

    // 當天下午 18:00
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT +  60 * 60 * 18);
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY);
    assertEquals(REAL_JUL_DAY_PLUS_HALF , halfAddedJulDay);
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 0.75, TimeTools.getJulDay(instant) , 0.0);   // jul day + 0.75

    // 當天下午 23:59
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT + 60 * 60 * 23 + 60 * 59 + 59);
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY);
    assertEquals(REAL_JUL_DAY_PLUS_HALF , halfAddedJulDay);

    // 當天晚上 24:00 , julDay 就 +1
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT + 60 * 60 * 23 + 60 * 59 + 60);
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY);
    assertEquals(REAL_JUL_DAY_PLUS_HALF+1 , halfAddedJulDay);
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 1, TimeTools.getJulDay(instant) , 0.0);   // jul day + 1
  }

  /**
   * 1582-10-15
   * Gregorian Cal 開始的 instant ，轉換到 LocalDateTime
   */
  @Test
  public void testInstantToLocalDateTime() {

    // Gregorian Cal 開始的 instant ，轉換到 LocalDateTime
    Instant instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT);
    JulDayResolver resolver = new JulDayResolver1582CutoverImpl();
    ChronoLocalDateTime dateTime = TimeTools.getLocalDateTime(instant , resolver);
    logger.info("dateTime , class = {} , value = {}" , dateTime.getClass() , dateTime);
    assertTrue(dateTime instanceof LocalDateTime);
    assertEquals(LocalDateTime.of(1582,10,15,0,0) , dateTime);

    // Gregorian Cal 前一秒，變成 Julian Day 1582-10-4 23:59:59
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT-1);

    Function<Double , ChronoLocalDateTime> fun = resolver::getLocalDateTime;
    dateTime = TimeTools.getLocalDateTime(instant , fun);

    logger.info("dateTime , class = {} , value = {}" , dateTime.getClass() , dateTime);
    assertTrue(dateTime instanceof JulianDateTime);
    assertEquals(JulianDateTime.of(1582,10,4,23,59,59) , dateTime);

  }

}
