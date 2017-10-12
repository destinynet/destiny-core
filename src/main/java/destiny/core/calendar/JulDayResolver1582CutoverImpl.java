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
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.IsoChronology;

/**
 * 1582-10-04 (含) 之前 , 傳回 {@link JulianChronology}
 * 1582-10-15 (含) 之後 , 傳回 {@link IsoChronology}
 * 其切分時間點，與 Java 的 {@link java.util.GregorianCalendar} 相同
 */
public class JulDayResolver1582CutoverImpl implements JulDayResolver, Serializable {

  private final static ZoneId GMT = ZoneId.of("GMT");

  /**
   * Julian Calendar    終止於西元 1582-10-04 , 該日的 Julian Day 是 2299159.5
   * Gregorian Calendar 開始於西元 1582-10-15 , 該日的 Julian Day 是 2299160.5
   * */
  private final static double GREGORIAN_START_JULIAN_DAY =2299160.5;

  /**
   * 承上， 西元 1582-10-15 0:0 的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
   */
  private final static long GREGORIAN_START_INSTANT = -12219292800000L;

  private static Logger logger = LoggerFactory.getLogger(JulDayResolver1582CutoverImpl.class);


  @Override
  public ChronoLocalDateTime getLocalDateTime(double gmtJulDay) {
    return getLocalDateTimeStatic(gmtJulDay);
  }

  @Override
  public ChronoLocalDateTime getLocalDateTime(Instant gmtInstant) {

    return getLocalDateTimeStatic(gmtInstant);
  }

  /**
   * @param gmtInstant 從 Instant 轉為 日期、時間
   */
  public static ChronoLocalDateTime getLocalDateTimeStatic(Instant gmtInstant) {
    long epochMilli = gmtInstant.toEpochMilli();
    logger.trace("epochMilli = {}" , epochMilli);
    boolean isGregorian = epochMilli >= GREGORIAN_START_INSTANT;
    if (isGregorian)
      return gmtInstant.atZone(GMT).toLocalDateTime();
    else {
      long epochSec = gmtInstant.getEpochSecond();
      int nanoOfSec = gmtInstant.getNano();
      logger.trace("epoch sec = {} , nanoOfSec = {} " , epochSec , nanoOfSec);
      return JulianDateTime.ofEpochSecond(epochSec , nanoOfSec , ZoneOffset.UTC);
    }
  }

  /**
   * 從 Julian Day 建立 {@link ChronoLocalDateTime} (GMT)
   * http://www.astro.com/ftp/placalc/src/revjul.c
   *
   * reverse function to julday()
   * 1582-10-15 0:00 為界
   * 之前，傳回 {@link JulianDateTime}
   * 之後，傳回 {@link LocalDateTime}
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

    Tuple2<Integer , Integer> pair = TimeTools.splitSecond(second);
    int secsInt = pair.v1();
    int nanoInt = pair.v2();

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

  /**
   * TODO : 註記 Gregorian or Julian
   * 利用一個字串 's' 來建立整個時間 , 格式如下：
   * 0123456789A1234567
   * +YYYYMMDDHHMMSS.SS
   * */
  public static ChronoLocalDateTime fromDebugString(String s) {
    boolean ad;
    char plusMinus = s.charAt(0);
    if (plusMinus == '+')
      ad = true;
    else if (plusMinus == '-')
      ad = false;
    else
      throw new RuntimeException("AD not correct : " + plusMinus);

    int yearOfEra = Integer.valueOf(s.substring(1, 5).trim());
    int month = Integer.valueOf(s.substring(5, 7).trim());
    int day = Integer.valueOf(s.substring(7, 9).trim());
    int hour = Integer.valueOf(s.substring(9, 11).trim());
    int minute = Integer.valueOf(s.substring(11, 13).trim());
    double second = Double.valueOf(s.substring(13));

    return of(ad , yearOfEra , month , day , hour , minute , second).v1();
  }

  public static Tuple2<ChronoLocalDateTime, Boolean> of(boolean ad , int yearOfEra , int month , int day , int hour , int minute , double second) {
    int prolepticYear = TimeTools.getNormalizedYear(ad , yearOfEra);

    boolean gregorian;
    Tuple2<Integer , Integer> pair = TimeTools.splitSecond(second);
    if (prolepticYear < 1582) {
      gregorian = false;
    } else if (prolepticYear > 1582) {
      gregorian = true;
    } else {
      // prolepticYear == 1582
      if (month < 10) {
        gregorian = false;
      } else if (month > 10) {
        gregorian = true;
      } else {
        // month == 10
        if (day <= 4)
          gregorian = false;
        else if (day >= 15)
          gregorian = true;
        else
          throw new RuntimeException("Error Date : " + prolepticYear + "/"+month+"/" + day);
      }
    }
    ChronoLocalDateTime ldt ;
    if (gregorian)
      ldt = LocalDateTime.of(prolepticYear , month , day , hour , minute , pair.v1(), pair.v2());
    else
      ldt = JulianDateTime.of(prolepticYear , month , day , hour , minute , pair.v1(), pair.v2());

    return Tuple.tuple(ldt , gregorian);
  }

}
