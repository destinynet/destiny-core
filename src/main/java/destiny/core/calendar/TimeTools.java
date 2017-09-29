/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRulesException;
import java.util.TimeZone;
import java.util.function.Function;

import static java.time.temporal.JulianFields.JULIAN_DAY;

public class TimeTools implements Serializable {

  private final static ZoneId GMT = ZoneId.of("GMT");

  /**
   * 西元 1582-10-15 0:0 的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
   */
  long GREGORIAN_START_INSTANT = -12219292800L;


  private static Logger logger = LoggerFactory.getLogger(TimeTools.class);


  // ======================================== GMT instant -> julian day ========================================

  /**
   * @param instant 從 「GMT」定義的 {@link Instant} 轉換成 Julian Day
   */
  public static double getJulDay(Instant instant) {
    // 先取得「被加上 0.5 的」 julian day
    ZonedDateTime zdt = instant.atZone(GMT);
    long halfAddedJulDay = zdt.getLong(JULIAN_DAY);
    LocalTime localTime = zdt.toLocalTime();
    return getGmtJulDay(halfAddedJulDay , localTime);
  }

  /**
   * @param instant 將 (GMT) instant 轉換為（GMT）的日期
   */
  public static ChronoLocalDateTime getLocalDateTime(Instant instant , Function<Double , ChronoLocalDateTime> resolver) {
    double gmtJulDay = getJulDay(instant);
    return resolver.apply(gmtJulDay);
  }

  /**
   * @param instant 將 (GMT) instant 轉換為（GMT）的日期
   *
   */
  public static ChronoLocalDateTime getLocalDateTime(Instant instant , JulDayResolver resolver) {
    Function<Double , ChronoLocalDateTime> fun = resolver::getLocalDateTime;
    return getLocalDateTime(instant , fun);
  }

  // ======================================== GMT DateTime -> julian day ========================================

  /**
   * astro julian day number 開始於
   * November 24, 4713 BC 當天中午  : proleptic Gregorian calendar
   * (proleptic year = -4712)
   * @param gmt 時刻 (包含 0 year)
   *                       getLong(JULIAN_DAY)   真正需要的值（左邊減 0.5）
   *  | ISO date          |  Julian Day Number | Astronomical Julian Day |
   *  | 1970-01-01T00:00  |         2,440,588  |         2,440,587.5     |
   *
   */
  public static double getGmtJulDay(ChronoLocalDateTime gmt) {
    Instant gmtInstant = gmt.toInstant(ZoneOffset.UTC);
    return getJulDay(gmtInstant);

    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 . 因此最後需要減去 0.5
//    long gmtJulDay_plusHalfDay = gmt.getLong(JULIAN_DAY);
//    return getGmtJulDay(gmtJulDay_plusHalfDay , gmt.toLocalTime());
  }


  /**
   * 承上， date + time 拆開來的版本
   */
  public static double getGmtJulDay(ChronoLocalDate date, LocalTime localTime) {
    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 . 因此最後需要減去 0.5
    long gmtJulDay_plusHalfDay = date.getLong(JULIAN_DAY);

    return getGmtJulDay(gmtJulDay_plusHalfDay , localTime);
  }


  // ======================================== LMT DateTime -> julian day ========================================


  /**
   * 直接從 LMT 傳回 gmt 的 jul day
   */
  public static double getGmtJulDay(ChronoLocalDateTime lmt , Location loc) {
    ChronoLocalDateTime gmt = getGmtFromLmt(lmt , loc);
    return getGmtJulDay(gmt);
  }




  // ======================================== LMT -> GMT ========================================
  public static ChronoLocalDateTime getGmtFromLmt(ChronoZonedDateTime zonedLmt) {
    ZoneOffset zoneOffset = zonedLmt.getOffset();
    int totalSeconds = zoneOffset.getTotalSeconds();
    return zonedLmt.toLocalDateTime().minus(totalSeconds , ChronoUnit.SECONDS);
  }

  public static ChronoLocalDateTime getGmtFromLmt(ChronoLocalDateTime lmt , ZoneId zoneId) {
    return getGmtFromLmt(lmt.atZone(zoneId));
  }

  public static ChronoLocalDateTime getGmtFromLmt(ChronoLocalDateTime lmt , Location loc) {
    if (loc.hasMinuteOffset()) {
      int secOffset = loc.getMinuteOffset() * 60;
      return lmt.plus(0-secOffset , ChronoUnit.SECONDS);
    } else {
      return getGmtFromLmt(lmt , loc.getTimeZone().toZoneId());
    }
  }

  /**
   * LMT (with TimeZone) to GMT
   *
   * ZoneId.of(string) 可能會出現 ZoneRulesException
   * 例如 : ZoneRulesException: Unknown time-zone ID: CTT
   * 因為某些 三字元的 zoneId 被 deprecated
   * 參照
   * http://stackoverflow.com/a/41683097/298430
   */
  public static ChronoLocalDateTime getGmtFromLmt(ChronoLocalDateTime lmt , TimeZone timeZone) {
    ZoneId zoneId = ZoneId.of("Asia/Taipei"); // 若無法 parse , 則採用 Asia/Taipei
    try {
      zoneId = ZoneId.of(timeZone.getID());
    } catch (ZoneRulesException ignored) {
    }
    return getGmtFromLmt(lmt , zoneId);
  }


  // ======================================== GMT -> LMT ========================================

  public static ChronoLocalDateTime getLmtFromGmt(ChronoLocalDateTime gmt , ZoneId zoneId) {
    ChronoZonedDateTime gmtZoned = gmt.atZone(GMT);
    logger.debug("gmtZoned = {}" , gmtZoned);
    ChronoZonedDateTime newZoned = gmtZoned.withZoneSameInstant(zoneId);
    logger.debug("gmtZoned with {} = {}" , zoneId , newZoned);
    return newZoned.toLocalDateTime();
  }

  public static ChronoLocalDateTime getLmtFromGmt(ChronoLocalDateTime gmt , Location loc) {
    if (loc.hasMinuteOffset()) {
      int secOffset = loc.getMinuteOffset() * 60;
      return gmt.plus(secOffset , ChronoUnit.SECONDS).atZone(loc.getTimeZone().toZoneId()).toLocalDateTime();
    }
    else {
      return getLmtFromGmt(gmt , loc.getTimeZone().toZoneId());
    }
  }

  public static ChronoLocalDateTime getLmtFromGmt(double gmtJulDay , Location location) {
    ChronoLocalDateTime gmt = JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(gmtJulDay);
    return getLmtFromGmt(gmt , location);
  }



  // ======================================== misc methods ========================================

  /**
   * 取得不中斷的年份 , 亦即 proleptic year
   * @param year 傳入的年，一定大於 0
   * @return proleptic year , 線性的 year : 西元前1年:0 , 西元前2年:-1 ...
   */
  public static int getNormalizedYear(boolean ad , int year) {
    if (year <= 0) {
      throw new RuntimeException("year " + year + " must > 0");
    }
    if (!ad)
      return -(year-1);
    else
      return year;
  }

  private static double getGmtJulDay(long halfAddedJulDay , LocalTime localTime) {
    int hour = localTime.getHour();
    int min = localTime.getMinute();
    int sec = localTime.getSecond();
    int nano = localTime.getNano();
    double dayValue = hour/24.0 + min/1440.0 + sec / 86400.0 + nano/(1_000_000_000.0 * 86400);

    return halfAddedJulDay - 0.5 + dayValue;
  }
}
