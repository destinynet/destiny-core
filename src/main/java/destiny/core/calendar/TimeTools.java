/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;

import static java.time.temporal.JulianFields.JULIAN_DAY;

public class TimeTools implements Serializable {

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
    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 . 因此最後需要減去 0.5
    long gmtJulDay_plusHalfDay = gmt.getLong(JULIAN_DAY);

    return getGmtJulDay(gmtJulDay_plusHalfDay , gmt.toLocalTime());
  }

  /**
   * 承上， date + time 拆開來的版本
   */
  public static double getGmtJulDay(ChronoLocalDate date, LocalTime localTime) {
    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 . 因此最後需要減去 0.5
    long gmtJulDay_plusHalfDay = date.getLong(JULIAN_DAY);

    return getGmtJulDay(gmtJulDay_plusHalfDay , localTime);
  }


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

  private static double getGmtJulDay(long gmtJulDay_plusHalfDay , LocalTime localTime) {
    int hour = localTime.getHour();
    int min = localTime.getMinute();
    int sec = localTime.getSecond();
    int nano = localTime.getNano();
    double dayValue = hour/24.0 + min/1440.0 + sec / 86400.0 + nano/(1_000_000_000.0 * 86400);

    return gmtJulDay_plusHalfDay + dayValue - 0.5;
  }
}
