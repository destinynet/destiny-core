/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import kotlin.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.JulianFields.JULIAN_DAY;

public class TimeTools implements Serializable {

  private final static ZoneId GMT = ZoneId.of("GMT");

  /**
   * 西元 1582-10-15 0:0 的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
   */
  long GREGORIAN_START_INSTANT = -12219292800L;


  private static Logger logger = LoggerFactory.getLogger(TimeTools.class);


  /**
   * ======================================== GMT {@link Instant} -> julian day {@link Double} ========================================
   */

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
   * 從「帶有 Zone」的時間，查詢當下的 julDay
   * 必須先轉為 GMT
   */
  public static double getJulDay(ChronoZonedDateTime zdt) {
    ChronoZonedDateTime gmt = zdt.withZoneSameInstant(GMT);
    return getGmtJulDay(gmt.toLocalDateTime());
  }


  /**
   * @param instant 將 (GMT) instant 轉換為（GMT）的日期
   */
  public static ChronoLocalDateTime getLocalDateTime(Instant instant , Function<Instant , ChronoLocalDateTime> revJulDayFunc) {
    return revJulDayFunc.apply(instant);
  }


  /**
   * @param instant 將 (GMT) instant 轉換為（GMT）的日期
   *
   */
  public static ChronoLocalDateTime getLocalDateTime(Instant instant , JulDayResolver resolver) {
    Function<Instant , ChronoLocalDateTime> fun = resolver::getLocalDateTime;
    return getLocalDateTime(instant , fun);
  }

  /**
   * ======================================== GMT {@link ChronoLocalDateTime} -> julian day {@link Double} ========================================
   */

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
  }


  /**
   * 承上， date + time 拆開來的版本
   */
  public static double getGmtJulDay(ChronoLocalDate date, LocalTime localTime) {
    return getGmtJulDay(date.atTime(localTime));
  }


  /**
   * ======================================== LMT {@link ChronoLocalDateTime} -> julian day {@link Double} ========================================
   */


  /**
   * 直接從 LMT 傳回 gmt 的 jul day
   */
  public static double getGmtJulDay(ChronoLocalDateTime lmt , ILocation loc) {
    ChronoLocalDateTime gmt = getGmtFromLmt(lmt , loc);
    return getGmtJulDay(gmt);
  }

  public static double getGmtJulDay(ChronoLocalDateTime lmt, ZoneId zoneId) {
    ChronoLocalDateTime gmt = getGmtFromLmt(lmt , zoneId);
    return getGmtJulDay(gmt);
  }




  // ======================================== LMT -> GMT ========================================
  public static ChronoZonedDateTime getGmtFromZonedDateTime(ChronoZonedDateTime zonedLmt) {
    ZoneOffset zoneOffset = zonedLmt.getOffset();
    int totalSeconds = zoneOffset.getTotalSeconds();
    return zonedLmt.toLocalDateTime().minus(totalSeconds , ChronoUnit.SECONDS).atZone(GMT);
  }

  public static ChronoLocalDateTime getGmtFromLmt(ChronoZonedDateTime zonedLmt) {
    ZoneOffset zoneOffset = zonedLmt.getOffset();
    int totalSeconds = zoneOffset.getTotalSeconds();
    return zonedLmt.toLocalDateTime().minus(totalSeconds , ChronoUnit.SECONDS);
  }

  public static ChronoLocalDateTime getGmtFromLmt(ChronoLocalDateTime lmt , ZoneId zoneId) {
    return getGmtFromLmt(lmt.atZone(zoneId));
  }

  public static ChronoLocalDateTime getGmtFromLmt(ChronoLocalDateTime lmt , ILocation loc) {
    if (loc.getHasMinuteOffset()) {
      int secOffset = loc.getFinalMinuteOffset() * 60;
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

  public static ChronoLocalDateTime getLmtFromGmt(ChronoLocalDateTime gmt , ILocation loc) {
    if (loc.getHasMinuteOffset()) {
      int secOffset = loc.getFinalMinuteOffset() * 60;
      return gmt.plus(secOffset , ChronoUnit.SECONDS).atZone(loc.getTimeZone().toZoneId()).toLocalDateTime();
    }
    else {
      return getLmtFromGmt(gmt , loc.getTimeZone().toZoneId());
    }
  }

  public static ChronoLocalDateTime getLmtFromGmt(double gmtJulDay , Location location , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    ChronoLocalDateTime gmt = revJulDayFunc.apply(gmtJulDay);
    return getLmtFromGmt(gmt , location);
  }



  // ======================================== DST 查詢 ========================================


  /**
   * @return 此時刻，此 TimeZone ，是否有日光節約時間
   */
  public static boolean isDst(ChronoLocalDateTime lmt, TimeZone tz) {
    ChronoZonedDateTime zdt = lmt.atZone(tz.toZoneId());
    return zdt.getZone().getRules().isDaylightSavings(zdt.toInstant());
  }

  public static boolean isDst(ChronoLocalDateTime lmt, Location loc) {
    return isDst(lmt , loc.getTimeZone());
  }

  public static int getSecondsOffset(ChronoLocalDateTime lmt , ZoneId zoneId) {
    return lmt.atZone(zoneId).getOffset().getTotalSeconds();
  }

  /**
   * @return 取得此地點、此時刻，與 GMT 的「秒差」 (不論是否有日光節約時間）
   *
   * 此演算法得到的結果，與下方相同
   * zoneId.getRules().getOffset(lmt.atZone(zoneId).toInstant()).getTotalSeconds();
   */
  private static int getSecondsOffset(ChronoLocalDateTime lmt, TimeZone tz) {
    return getSecondsOffset(lmt , tz.toZoneId());
//    ZoneOffset offset = lmt.atZone(tz.toZoneId()).getOffset();
//    return offset.getTotalSeconds();
  }

  /**
   * @return 取得此地點、此時刻，與 GMT 的「秒差」 (不論是否有日光節約時間）
   */
  public static int getSecondsOffset(ChronoLocalDateTime lmt, Location loc) {
    return getSecondsOffset(lmt , loc.getTimeZone());
  }

  /**
   * @return 確認此時刻，是否有DST。不論是否有沒有DST，都傳回與GMT誤差幾秒
   * */
  public static Pair<Boolean, Integer> getDstSecondOffset(@NotNull ChronoLocalDateTime lmt, @NotNull Location loc) {
    return new Pair<>(isDst(lmt, loc), getSecondsOffset(lmt, loc));
  }

  // ======================================== misc methods ========================================

  /**
   * @return 確認 later 是否真的 after prior 的時刻
   * 相當於 {@link ChronoLocalDateTime#isAfter(ChronoLocalDateTime)}
   */
  public static boolean isAfter(ChronoLocalDateTime later , ChronoLocalDateTime prior) {
    double smaller = getGmtJulDay(prior);
    double bigger = getGmtJulDay(later);
    return bigger > smaller;
  }

  public static boolean isBefore(ChronoLocalDateTime prior , ChronoLocalDateTime later) {
    double bigger = getGmtJulDay(later);
    double smaller = getGmtJulDay(prior);
    return smaller < bigger;
  }


  /**
   * @return t 是否 處於 t1 與 t2 之間
   *
   * 將這些 t , t1 , t2 視為 GMT , 轉成 jul day 來比較大小
   */
  public static boolean isBetween(ChronoLocalDateTime t , ChronoLocalDateTime t1 , ChronoLocalDateTime t2) {
    double julDay = getGmtJulDay(t);
    double julDay1 = getGmtJulDay(t1);
    double julDay2 = getGmtJulDay(t2);
    return (julDay2 > julDay && julDay > julDay1) || (julDay1 > julDay && julDay > julDay2);
  }


  public static String getDebugString(ChronoLocalDateTime time) {
    StringBuilder sb = new StringBuilder();
    sb.append(time.get(YEAR_OF_ERA) >= 1 ? '+' : '-');
    sb.append(StringUtils.leftPad(String.valueOf(time.get(YEAR_OF_ERA)), 4, ' '));
    sb.append(StringUtils.leftPad(String.valueOf(time.get(MONTH_OF_YEAR)), 2, ' '));
    sb.append(StringUtils.leftPad(String.valueOf(time.get(DAY_OF_MONTH)), 2, ' '));
    sb.append(StringUtils.leftPad(String.valueOf(time.get(HOUR_OF_DAY)), 2, ' '));
    sb.append(StringUtils.leftPad(String.valueOf(time.get(MINUTE_OF_HOUR)), 2, ' '));
    sb.append(time.get(SECOND_OF_MINUTE));
    sb.append('.');
    if (time.get(NANO_OF_SECOND) == 0) {
      sb.append('0');
    } else {
      // 小數點部分
      String decimal = String.valueOf(time.get(NANO_OF_SECOND) / 1_000_000_000.0);
      sb.append(decimal.substring(2));
    }
    return sb.toString();
  }

  /** 將 double 的秒數，拆為 long秒數 以及 longNano 兩個值 */
  public static Pair<Integer , Integer> splitSecond(double seconds) {
    int secs = (int) seconds;
    int nano = (int) ((seconds - secs)* 1_000_000_000);
    return new Pair<>(secs , nano);
  }

  /**
   * 將「分鐘」拆成「小時」與「分」
   */
  public static Pair<Integer , Integer> splitMinutes(int minutes) {
    int hours = minutes / 60;
    int mins = minutes % 60;
    return new Pair<>(hours , mins);
  }

  /**
   * 取得不中斷的年份 , 亦即 proleptic year
   * @param yearOfEra 傳入的年，一定大於 0
   * @return proleptic year , 線性的 year : 西元前1年:0 , 西元前2年:-1 ...
   */
  public static int getNormalizedYear(boolean ad , int yearOfEra) {
    if (yearOfEra <= 0) {
      throw new RuntimeException("year " + yearOfEra + " must > 0");
    }
    if (!ad)
      return -(yearOfEra-1);
    else
      return yearOfEra;
  }

  private static double getGmtJulDay(long halfAddedJulDay , LocalTime localTime) {
    int hour = localTime.getHour();
    int min = localTime.getMinute();
    int sec = localTime.getSecond();
    int nano = localTime.getNano();
    double dayValue = hour/24.0 + min/1440.0 + sec / 86400.0 + nano/(1_000_000_000.0 * 86400);

    return halfAddedJulDay - 0.5 + dayValue;
  }

  /**
   * 將 LMT 以及經度 轉換為當地真正的時間 , 不包含真太陽時(均時差) 的校正
   * @return 經度時間
   */
  public static ChronoLocalDateTime getLongitudeTime(ChronoLocalDateTime lmt, Location location) {
    double absLng = Math.abs(location.getLng());
    double secondsOffset = getDstSecondOffset(lmt, location).getSecond();
    double zoneSecondOffset = Math.abs(secondsOffset);
    double longitudeSecondOffset = absLng * 4 * 60; // 經度與GMT的時差 (秒) , 一分鐘四度

    if (location.getEastWest()==EastWest.EAST) {
      double seconds = longitudeSecondOffset - zoneSecondOffset;
      Pair<Integer , Integer> pair = splitSecond(seconds);
      return lmt.plus(pair.getFirst() , ChronoUnit.SECONDS).plus(pair.getSecond() , ChronoUnit.NANOS);
    } else {
      double seconds = zoneSecondOffset - longitudeSecondOffset;
      Pair<Integer , Integer> pair = splitSecond(seconds);
      return lmt.plus(pair.getFirst() , ChronoUnit.SECONDS).plus(pair.getSecond() , ChronoUnit.NANOS);
    }
  }


}
