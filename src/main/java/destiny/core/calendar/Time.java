/**
 * Created on 2002/8/16 at 下午 06:11:28
 * 2007/05/25 解決了 Gregorian/Julian Calendar 自動轉換的問題, 設定 1582/10/4 之後跳到 1582/10/15
 */
package destiny.core.calendar;

import destiny.tools.AlignUtil;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.IsoEra;
import java.util.TimeZone;

import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static org.jooq.lambda.tuple.Tuple.tuple;

/**
 * 代表 『時間』 的物件
 * 1582/10/4 之後跳到 1582/10/15 , 之前是 Julian Calendar , 之後是 Gregorian Calendar
 */
public class Time implements Serializable {

  /**
   * Julian Calendar    終止於西元 1582-10-04 , 該日的 Julian Day 是 2299159.5
   * Gregorian Calendar 開始於西元 1582-10-15 , 該日的 Julian Day 是 2299160.5
   * */
  private final static double GREGORIAN_START_JULIAN_DAY =2299160.5;
  
  private static Logger logger = LoggerFactory.getLogger(Time.class);

  /**
   *
   * @param isAD CE : true / BC : false
   * @param year 一定 > 0
   */
  public static LocalDateTime ofLocalDateTime(boolean isAD , int year , int month , int day , int hour , int minute , double second) {
    if (year <=0 )
      throw new RuntimeException("year("+year+") <= 0");

    int prolepticYear = year; // 可能 <= 0
    if (!isAD) {
      prolepticYear = -(year - 1);
    }

    LocalDateTime result;
    LocalDate localDate = LocalDate.of(prolepticYear , month , day);
    Tuple2<Long , Long> pair = Time.splitSecond(second);
    LocalTime localTime = LocalTime.of(hour , minute, pair.v1().intValue() , pair.v2().intValue());
    if (isAD) {
      result = LocalDateTime.of(localDate , localTime);
    } else {
      result = LocalDateTime.of(localDate.with(IsoEra.BCE) , localTime);
    }
    return  result;
  }


  /**
   * TODO : 註記 Gregorian or Julian
   * 利用一個字串 's' 來建立整個時間 , 格式如下： 
   * 0123456789A1234567
   * +YYYYMMDDHHMMSS.SS
   * */
  public static LocalDateTime fromDebugString(String s) {
    boolean ad;
    char plusMinus = s.charAt(0);
    if (plusMinus == '+')
      ad = true;
    else if (plusMinus == '-')
      ad = false;
    else
      throw new RuntimeException("AD not correct : " + plusMinus);

    int year = Integer.valueOf(s.substring(1, 5).trim());
    int month = Integer.valueOf(s.substring(5, 7).trim());
    int day = Integer.valueOf(s.substring(7, 9).trim());
    int hour = Integer.valueOf(s.substring(9, 11).trim());
    int minute = Integer.valueOf(s.substring(11, 13).trim());
    double second = Double.valueOf(s.substring(13));

    Tuple2<Long , Long> pair = Time.splitSecond(second);

    int prolepticYear = TimeTools.getNormalizedYear(ad , year);

    return LocalDateTime.of(prolepticYear , month , day , hour , minute , pair.v1().intValue() , pair.v2().intValue());
  }

  /** 傳回最精簡的文字表示法 , 可以餵進去 {@link #fromDebugString(String)} 裡面*/
  public static String getDebugString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();
    sb.append(time.getYear() >= 1 ? '+' : '-');
    sb.append(AlignUtil.alignRight(time.get(YEAR_OF_ERA), 4, ' '));
    sb.append(AlignUtil.alignRight(time.getMonthValue(), 2, ' '));
    sb.append(AlignUtil.alignRight(time.getDayOfMonth(), 2, ' '));
    sb.append(AlignUtil.alignRight(time.getHour(), 2, ' '));
    sb.append(AlignUtil.alignRight(time.getMinute(), 2, ' '));
    sb.append(time.getSecond());
    sb.append('.');
    if (time.getNano() == 0) {
      sb.append('0');
    } else {
      // 小數點部分
      String decimal = String.valueOf(time.getNano() / 1_000_000_000.0);
      sb.append(decimal.substring(2));
    }
    return sb.toString();
  }


  /**
   * @return t 是否介於 t1 與 t2 之間
   */
  public static boolean isBetween(LocalDateTime t , LocalDateTime t1 , LocalDateTime t2) {
    return
      (
        (t2.isAfter(t1) && t.isAfter(t1) && t2.isAfter(t)) ||
        (t1.isAfter(t2) && t.isAfter(t2) && t1.isAfter(t))
      );
  }

  public static boolean isBetween(ChronoLocalDateTime t , ChronoLocalDateTime t1 , ChronoLocalDateTime t2) {
    return
      (
        (t2.isAfter(t1) && t.isAfter(t1) && t2.isAfter(t)) ||
        (t1.isAfter(t2) && t.isAfter(t2) && t1.isAfter(t))
      );
  }


  

  /**
   * @return 取得此地點、此時刻，與 GMT 的「秒差」 (不論是否有日光節約時間）
   */
  private static int getSecondsOffset(ChronoLocalDateTime lmt, TimeZone tz) {
    ZoneOffset offset = lmt.atZone(tz.toZoneId()).getOffset();
    return offset.getTotalSeconds();
  }

  /**
   * @return 取得此地點、此時刻，與 GMT 的「秒差」 (不論是否有日光節約時間）
   */
  public static int getSecondsOffset(LocalDateTime lmt, Location loc) {
    return getSecondsOffset(lmt , loc.getTimeZone());
  }

  /**
   * @return 此時刻，此 TimeZone ，是否有日光節約時間
   */
  private static boolean isDst(LocalDateTime lmt, TimeZone tz) {
    ZonedDateTime zdt = lmt.atZone(tz.toZoneId());
    return zdt.getZone().getRules().isDaylightSavings(zdt.toInstant());
  }

  /**
   * @return 此時刻，此 地點，是否有日光節約時間
   */
  private static boolean isDst(LocalDateTime lmt, Location loc) {
    return isDst(lmt , loc.getTimeZone());
  }

  /**
   * @return 確認此時刻，是否有DST。不論是否有沒有DST，都傳回與GMT誤差幾秒
   * */
  public static Tuple2<Boolean, Integer> getDstSecondOffset(@NotNull ChronoLocalDateTime lmt, @NotNull Location loc) {
    return tuple(Time.isDst((LocalDateTime)lmt, loc), Time.getSecondsOffset((LocalDateTime)lmt, loc));
  }



  public static double getGmtJulDay(boolean isAd , boolean isGregorian , int year , int month , int day , int hour , int minute , double second) {
    double thisHour = hour + ((double)minute) / 60 +  second / 3600;
    double jd;
    double u, u0, u1, u2;

    u = TimeTools.getNormalizedYear(isAd , year);

    if (month < 3)
    {
      u -= 1;
    }
    u0 = u + 4712.0;
    u1 = month + 1.0;
    if (u1 < 4)
    {
      u1 += 12.0;
    }
    jd = Math.floor(u0 * 365.25) + Math.floor(30.6 * u1 + 0.000001) + day + thisHour / 24.0 - 63.5;
    if (isGregorian) {
      u2 = Math.floor(Math.abs(u) / 100) - Math.floor(Math.abs(u) / 400);
      if (u < 0.0) {
        u2 = -u2;
      }
      jd = jd - u2 + 2;
      if ((u < 0.0) && (u / 100 == Math.floor(u / 100)) && (u / 400 != Math.floor(u / 400))) {
        jd -= 1;
      }
    }
    return jd;
  }

  /** 將 double 的秒數，拆為 long秒數 以及 longNano 兩個值 */
  public static Tuple2<Long , Long> splitSecond(double seconds) {
    long secs = (long) seconds;
    long nano = (long) ((seconds - secs)* 1_000_000_000);
    return tuple(secs , nano);
  }

  //////////////////////////////////////////////////////////////////////
  // Erzeugt aus einem jd/calType Jahr, Monat, Tag und Stunde.        //
  // It does NOT change any global variables.                         //
  //////////////////////////////////////////////////////////////////////
  @NotNull
  private static synchronized IDate swe_revjul(double jd, boolean calType)
  {
    IDate dt=new IDate();
    double u0,u1,u2,u3,u4;

    u0 = jd + 32082.5;
    if (calType)
    {
      u1 = u0 + Math.floor (u0/36525.0) - Math.floor (u0/146100.0) - 38.0;
      if (jd >= 1830691.5) 
      {
        u1 +=1;
      }
      u0 = u0 + Math.floor (u1/36525.0) - Math.floor (u1/146100.0) - 38.0;
    }
    u2 = Math.floor (u0 + 123.0);
    u3 = Math.floor ( (u2 - 122.2) / 365.25);
    u4 = Math.floor ( (u2 - Math.floor (365.25 * u3) ) / 30.6001);
    dt.month = (int) (u4 - 1.0);
    if (dt.month > 12) 
    {
      dt.month -= 12;
    }
    dt.day = (int) (u2 - Math.floor (365.25 * u3) - Math.floor (30.6001 * u4));
    dt.year = (int) (u3 + Math.floor ( (u4 - 2.0) / 12.0) - 4800);
    dt.hour = (jd - Math.floor (jd + 0.5) + 0.5) * 24.0;
    return dt;
  }

}

class IDate implements java.io.Serializable
{
  public int year;
  public int month;
  public int day;
  public double hour;
}
