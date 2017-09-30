/**
 * Created on 2002/8/16 at 下午 06:11:28
 * 2007/05/25 解決了 Gregorian/Julian Calendar 自動轉換的問題, 設定 1582/10/4 之後跳到 1582/10/15
 */
package destiny.core.calendar;

import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.IsoEra;

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
    Tuple2<Long , Long> pair = TimeTools.splitSecond(second);
    LocalTime localTime = LocalTime.of(hour , minute, pair.v1().intValue() , pair.v2().intValue());
    if (isAD) {
      result = LocalDateTime.of(localDate , localTime);
    } else {
      result = LocalDateTime.of(localDate.with(IsoEra.BCE) , localTime);
    }
    return  result;
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
