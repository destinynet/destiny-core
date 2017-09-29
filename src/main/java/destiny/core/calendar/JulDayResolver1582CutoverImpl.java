/**
 * Created by smallufo on 2017-09-25.
 */
package destiny.core.calendar;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.chrono.JulianChronology;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.IsoChronology;

/**
 * 1582-10-04 (含) 之前 , 傳回 {@link JulianChronology}
 * 1582-10-15 (含) 之後 , 傳回 {@link IsoChronology}
 * 其切分時間點，與 Java 的 {@link java.util.GregorianCalendar} 相同
 */
public class JulDayResolver1582CutoverImpl implements JulDayResolver, Serializable {

  /**
   * Julian Calendar    終止於西元 1582-10-04 , 該日的 Julian Day 是 2299159.5
   * Gregorian Calendar 開始於西元 1582-10-15 , 該日的 Julian Day 是 2299160.5
   * */
  private final static double GREGORIAN_START_JULIAN_DAY =2299160.5;

  /**
   * 承上， 西元 1582-10-15 0:0 的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
   */
  private final static long GREGORIAN_START_INSTANT = -12219292800L;

  private static Logger logger = LoggerFactory.getLogger(JulDayResolver1582CutoverImpl.class);


  @Override
  public ChronoLocalDateTime getLocalDateTime(double gmtJulDay) {
    return getLocalDateTimeStatic(gmtJulDay);
  }


  /**
   * 從 Julian Day 建立 {@link ChronoLocalDateTime} (GMT)
   * http://www.astro.com/ftp/placalc/src/revjul.c
   *
   * inverse function to julday()
   */
  public static ChronoLocalDateTime getLocalDateTimeStatic(double gmtJulDay) {
    boolean isGregorian = false;

    if (gmtJulDay >= GREGORIAN_START_JULIAN_DAY) {
      isGregorian = true;
    }

    double u0,u1,u2,u3,u4;

    u0 = gmtJulDay + 32082.5;

    if (isGregorian) {
      u1 = u0 + Math.floor(u0 / 36525.0) - Math.floor(u0 / 146100.0) - 38.0;
      if (gmtJulDay >= 1830691.5) {
        u1 += 1;
      }
      u0 = u0 + Math.floor(u1 / 36525.0) - Math.floor(u1 / 146100.0) - 38.0;
    }
    u2 = Math.floor(u0 + 123.0);
    u3 = Math.floor((u2 - 122.2) / 365.25);
    u4 = Math.floor((u2 - Math.floor(365.25 * u3)) / 30.6001);
    int month = (int) (u4 - 1.0);
    if (month > 12) {
      month -= 12;
    }
    int day = (int) (u2 - Math.floor(365.25 * u3) - Math.floor(30.6001 * u4));
    int y = (int) (u3 + Math.floor((u4 - 2.0) / 12.0) - 4800);

    boolean ad = true;
    int year;

    if (y <= 0) {
      ad = false;
      year = -(y - 1); // 取正值
    }
    else {
      year = y;
    }

    double h = (gmtJulDay - Math.floor(gmtJulDay + 0.5) + 0.5) * 24.0;
    int hour = (int) h;
    int minute = (int) (h * 60 - hour * 60);
    double second = h * 3600 - hour * 3600 - minute * 60;

    Tuple2<Long , Long> pair = Time.splitSecond(second);
    int secsInt = pair.v1().intValue();
    int nanoInt = pair.v2().intValue();

    LocalTime localTime = LocalTime.of(hour , minute , secsInt , nanoInt);

    if (isGregorian) {
      // ad 一定為 true , 不用考慮負數年數
      return LocalDate.of(year , month , day).atTime(localTime);
    } else {
      int prolepticYear = TimeTools.getNormalizedYear(ad , year);
      return JulianDateTime.of(prolepticYear , month , day , localTime.getHour() , localTime.getMinute() , localTime.getSecond() , localTime.getNano());
    }
  }

  public static Tuple2<ChronoLocalDate , LocalTime> getDateTime(double gmtJulDay) {
    ChronoLocalDateTime dateTime = getLocalDateTimeStatic(gmtJulDay);
    return Tuple.tuple(dateTime.toLocalDate() , dateTime.toLocalTime());
  }


}
