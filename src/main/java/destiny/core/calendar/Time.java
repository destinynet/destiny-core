/**
 * Created on 2002/8/16 at 下午 06:11:28
 * 2007/05/25 解決了 Gregorian/Julian Calendar 自動轉換的問題, 設定 1582/10/4 之後跳到 1582/10/15
 */
package destiny.core.calendar;

import destiny.tools.AlignUtil;
import destiny.tools.LocaleStringIF;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.chrono.JulianDate;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.TimeZone;

import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.JulianFields.JULIAN_DAY;

/**
 * 代表 『時間』 的物件
 * 1582/10/4 之後跳到 1582/10/15 , 之前是 Julian Calendar , 之後是 Gregorian Calendar
 */
public class Time implements Serializable , LocaleStringIF , DateIF
{
  /* 是否是西元「後」, 
  西元前為 false , 西元後為 true (default) */
  protected boolean ad = true; 
  protected int year;
  protected int month;
  protected int day;
  protected int hour;
  protected int minute;
  protected double second;
  
  /**
   * Julian Calendar    終止於西元 1582-10-04 , 該日的 Julian Day 是 2299159.5
   * Gregorian Calendar 開始於西元 1582-10-15 , 該日的 Julian Day 是 2299160.5
   * */
  private final static double GREGORIAN_START_JULIAN_DAY =2299160.5;
  
  /** 
   * 內定是 Gregorian Calendar <BR/>
   * Gregorian == TRUE <BR/>
   * Julian == FALSE
   * */
  protected boolean gregorian = true ;

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
    Pair<Long , Long> pair = Time.splitSecond(second);
    LocalTime localTime = LocalTime.of(hour , minute, pair.getLeft().intValue() , pair.getRight().intValue());
    if (isAD) {
      result = LocalDateTime.of(localDate , localTime);
    } else {
      result = LocalDateTime.of(localDate.with(IsoEra.BCE) , localTime);
    }
    return  result;
  }


  /**
   * TODO : 解決所有 Julian Calendar 的問題
   */
  public Time(boolean isAD , int year , int month , int day , int hour , int minute , double second ) {
    if (year <= 0)
      throw new RuntimeException("Year cannot be less than or equal to 0 in this constructor!");
    this.ad = isAD;
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
    this.minute = minute;
    this.second = second;
    checkDate();
    this.normalize();
  }


  @Deprecated
  public Time(int year, int month, int day, int hour, int minute, double second) {
    this(year > 0 ,
      (year <=0 ? (-(year-1)) : year) ,
      month , day , hour , minute , second);
  }

  public Time(int year, int month, int day) {
    this(year, month, day, 0, 0, 0);
  }

  
  /** 
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

    Pair<Long , Long> pair = Time.splitSecond(second);

    int prolepticYear = getNormalizedYear(ad , year);

    return LocalDateTime.of(prolepticYear , month , day , hour , minute , pair.getLeft().intValue() , pair.getRight().intValue());
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



  public static Pair<ChronoLocalDate , LocalTime> from(double gmtJulDay) {
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

    Pair<Long , Long> pair = splitSecond(second);
    int secsInt  = pair.getLeft().intValue();
    int nanoInt = pair.getRight().intValue();

    LocalTime localTime = LocalTime.of(hour , minute , secsInt , nanoInt);

    if (isGregorian) {
      // ad 一定為 true , 不用考慮負數年數
      return Pair.of(LocalDate.of(year , month , day) , localTime);
    } else {
      int prolepticYear = getNormalizedYear(ad , year);
      return Pair.of(JulianDate.of(prolepticYear , month , day) ,localTime);
    }
  }
  
  public Time(double julianDay) {
    this(julianDay , (julianDay >= GREGORIAN_START_JULIAN_DAY));
  }
  
  /**
   * 從 Julian Day 建立 Time
   * http://www.astro.com/ftp/placalc/src/revjul.c
   *
   * inverse function to julday()
   */
  public Time(double julianDay , boolean isGregorian) {
    double u0,u1,u2,u3,u4;

    u0 = julianDay + 32082.5;
    this.gregorian = isGregorian;
    if (isGregorian) {
      u1 = u0 + Math.floor(u0 / 36525.0) - Math.floor(u0 / 146100.0) - 38.0;
      if (julianDay >= 1830691.5) { // 西元300年2月29日 (0AM)？
        u1 += 1;
      }
      u0 = u0 + Math.floor(u1 / 36525.0) - Math.floor(u1 / 146100.0) - 38.0;
    }
    u2 = Math.floor(u0 + 123.0);
    u3 = Math.floor((u2 - 122.2) / 365.25);
    u4 = Math.floor((u2 - Math.floor(365.25 * u3)) / 30.6001);
    this.month = (int) (u4 - 1.0);
    if (this.month > 12) {
      this.month -= 12;
    }
    this.day = (int) (u2 - Math.floor(365.25 * u3) - Math.floor(30.6001 * u4));
    int y = (int) (u3 + Math.floor((u4 - 2.0) / 12.0) - 4800);
    if (y <= 0) {
      this.ad = false;
      this.year = -(y - 1); // 取正值
    }
    else {
      this.year = y;
    }

    double h = (julianDay - Math.floor(julianDay + 0.5) + 0.5) * 24.0;
    this.hour = (int) h;
    this.minute = (int) (h * 60 - hour * 60);
    this.second = h * 3600 - hour * 3600 - minute * 60;
  }





  /**
   * 檢查時間是否落入 1582/10/5~14 之間 , 如果有 , 就丟出 Exception
   */
  private void checkDate() {
    if (this.isAd() && year == 1582 && month == 10 && (day >= 5 && day <= 14))
      throw new RuntimeException("Invalid time! Date between 1582/10/5(inclusive) and 1582/10/14(inclusive) is invalid.");
    this.gregorian = getGmtJulDay() >= GREGORIAN_START_JULIAN_DAY;
//    if (getGmtJulDay() >= GREGORIAN_START_JULIAN_DAY )
//      this.gregorian = true;
//    else
//      this.gregorian = false;
  }


  public LocalDateTime toLocalDateTime() {
    return Time.ofLocalDateTime(ad, year, month, day, hour, minute, second);
  }

  /**
   * TODO : 檢查 1582 的轉換
   */
  public static Time from(LocalDateTime ldt) {
    return new Time(ldt.toLocalDate().getEra() == IsoEra.CE , ldt.get(YEAR_OF_ERA) , ldt.getMonthValue() , ldt.getDayOfMonth() , ldt.getHour() , ldt.getMinute() , ldt.getSecond());
  }
  
  
  /** 取得西元年份，注意，這裡的傳回值不可能小於等於0 */
  @Override
  public int getYear() { return this.year; }

  /**
   * 取得不中斷的年份
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

  @Override
  public int getMonth() { return this.month; }
  
  @Override
  public int getDay() { return this.day; }

  public boolean isGregorian() { return gregorian; }
  
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

  private void normalize() {
    long skips = 0; //進位
    if (second >= 60) {
      skips = (long) (second / 60);
      second = second - skips * 60;
    }
    else if (second < 0) {
      skips = (long) (second / 60) - 1;
      if (second % 60 == 0)
        skips++;
      second = second - skips * 60;
    }
    minute += skips;

    skips = 0;
    if (minute >= 60) {
      skips = (minute / 60);
      minute = (int) (minute - skips * 60);
    }
    else if (minute < 0) {
      skips = (minute / 60) - 1;
      if (minute % 60 == 0)
        skips++;
      minute = (int) (minute - skips * 60);
    }
    hour += skips;

    skips = 0;

    if (hour >= 24) {
      skips = (hour / 24);
      hour = (int) (hour - skips * 24);
    }
    else if (hour < 0) {
      skips = (hour / 24) - 1;
      if (hour % 24 == 0)
        skips++;
      hour = (int) (hour - skips * 24);
    }
    double julday = this.getGmtJulDay();
    julday += skips;


    Time t2 = new Time(julday);

//    this.second = t2.getSecond();
//    this.minute = t2.getMinute();
//    this.hour = t2.getHour();

    this.day = t2.getDay();
    this.month = t2.getMonth();
    this.year = t2.getYear();
    this.ad = t2.isAd();
    //this.Gregorian = t2.isGregorian();
    if (!ad)
      this.gregorian = isGregorian(-(t2.getYear()-1) , t2.getMonth() , t2.getDay());
    else
      this.gregorian = isGregorian(t2.getYear() , t2.getMonth() , t2.getDay());
    
  }//normalize

  
  /**
   * 與 Gregorian Calendar 的啟始日比對 , 判斷輸入的日期是否是 Gregorian Calendar
   */
  private static boolean isGregorian(int year, int month, int day) {
    IDate dt=swe_revjul(GREGORIAN_START_JULIAN_DAY,true);
    boolean isGregorian= true;
    if (dt.year > year ||
        (dt.year == year && dt.month > month) ||
        (dt.year == year && dt.month == month && dt.day > day)) 
    {
      isGregorian = false;
    }
    return isGregorian;
  }
  

  /**
   * LMT (with TimeZone) to GMT
   */
  public static LocalDateTime getGmtFromLmt(LocalDateTime lmt , TimeZone timeZone) {
    ZonedDateTime ldtZoned = lmt.atZone(ZoneId.of(timeZone.getID()));
    ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
    return utcZoned.toLocalDateTime();
  }

  /**
   * LMT (with Location) to GMT
   */
  public static LocalDateTime getGmtFromLmt(LocalDateTime lmt , @NotNull Location loc) {
    if (loc.isMinuteOffsetSet()) {
      int secOffset = loc.getMinuteOffset() * 60;
      return lmt.plus(0-secOffset , ChronoUnit.SECONDS);
    }
    else {
      return getGmtFromLmt(lmt , loc.getTimeZone());
    }
  }

  public static LocalDateTime getLmtFromGmt(LocalDateTime gmt, TimeZone timeZone) {
    ZonedDateTime gmtZoned = gmt.atZone(ZoneId.of("UTC"));
    ZonedDateTime ldtZoned = gmtZoned.withZoneSameInstant(timeZone.toZoneId());
    return ldtZoned.toLocalDateTime();
  }

  public static LocalDateTime getLmtFromGmt(LocalDateTime gmt , @NotNull Location loc) {
    if (loc.isMinuteOffsetSet()) {
      int secOffset = loc.getMinuteOffset() * 60;
      return gmt.plus(secOffset , ChronoUnit.SECONDS);
    }
    else {
      return getLmtFromGmt(gmt , loc.getTimeZone());
    }
  }

  /**
   * @return 取得此地點、此時刻，與 GMT 的「秒差」 (不論是否有日光節約時間）
   */
  public static int getSecondsOffset(LocalDateTime lmt, TimeZone tz) {
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
  public static boolean isDst(LocalDateTime lmt , TimeZone tz) {
    ZonedDateTime zdt = lmt.atZone(tz.toZoneId());
    return zdt.getZone().getRules().isDaylightSavings(zdt.toInstant());
  }

  /**
   * @return 此時刻，此 地點，是否有日光節約時間
   */
  public static boolean isDst(LocalDateTime lmt , Location loc) {
    return isDst(lmt , loc.getTimeZone());
  }

  /**
   * @return 確認此時刻，是否有DST。不論是否有沒有DST，都傳回與GMT誤差幾秒
   * */
  public static Pair<Boolean, Integer> getDstSecondOffset(@NotNull LocalDateTime lmt, @NotNull Location loc) {
    return ImmutablePair.of(Time.isDst(lmt , loc), Time.getSecondsOffset(lmt , loc));
  }
  
  @Override
  public String toString() {
    return TimeDecorator.getOutputString(toLocalDateTime() , Locale.getDefault());
  }
  
  public String toString(Locale locale) {
    return TimeDecorator.getOutputString(toLocalDateTime() , locale);
  }

  /**
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    if (gregorian)
      sb.append("G");
    else
      sb.append("J");
      
    if (AD == false)
      sb.append("(-) " );
    else
      sb.append("(+) ");
    sb.append(this.year + "/"+ (this.month < 10 ? "0" : "" )+ this.month+"/"+ (this.day < 10 ? "0" : "" ) + this.day+" "+ (this.hour < 10 ? "0" : "") +this.hour+":"+ (this.minute < 10 ? "0" : "" ) + this.minute+":"+ ( this.second < 10  && this.second >=0 ? "0" : "" ) + this.second + "\t" + this.getGmtJulDay() );
    return sb.toString();    
  }
  */

  @Override
  public boolean equals(@Nullable Object o) {
    if ((o != null) && (o.getClass().equals(this.getClass()))) {
      Time time = (Time) o;
      return (this.ad == time.ad &&
          this.year == time.year &&
          this.month == time.month &&
          this.day == time.day &&
          this.hour == time.hour &&
          this.minute == time.minute &&
          this.second == time.second &&
          this.gregorian == time.gregorian
          );
    }
    else return false;
  }
  
  /**
   * 將目前的 Time 視為 GMT , 取得目前這個 Time 的 Julian Day Number
   * <BR> Julian Day : 零時為 0.5 , 中午 12 時才是整數 （這是因為起點是中午十二點）
   * @return Julian Day
   */
  public double getGmtJulDay() {
    return getGmtJulDay(ad , gregorian , year , month , day , hour , minute , second);
//    double thisHour = hour + ((double) minute) / 60 + second / 3600;
//
//    double jd;
//    double u, u0, u1, u2;
//
//    u = getNormalizedYear();
//
//    if (month < 3) {
//      u -= 1;
//    }
//    u0 = u + 4712.0;
//    u1 = month + 1.0;
//    if (u1 < 4) {
//      u1 += 12.0;
//    }
//    jd = Math.floor(u0 * 365.25) + Math.floor(30.6 * u1 + 0.000001) + day + thisHour / 24.0 - 63.5;
//    if (gregorian) {
//      u2 = Math.floor(Math.abs(u) / 100) - Math.floor(Math.abs(u) / 400);
//      if (u < 0.0) {
//        u2 = -u2;
//      }
//      jd = jd - u2 + 2;
//      if ((u < 0.0) && (u / 100 == Math.floor(u / 100)) && (u / 400 != Math.floor(u / 400))) {
//        jd -= 1;
//      }
//    }
//    return jd;
  }

  /**
   * @param gmt proleptic Gregorian (包含 0 year)
   *                       getLong(JULIAN_DAY)   真正需要的值（左邊減 0.5）
   *  | ISO date          |  Julian Day Number | Astronomical Julian Day |
   *  | 1970-01-01T00:00  |         2,440,588  |         2,440,587.5     |
   */
  public static double getGmtJulDay(LocalDateTime gmt) {
    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 . 因此最後需要減去 0.5
    long gmtJulDay_plusHalfDay = gmt.toLocalDate().getLong(JULIAN_DAY);

    return inner_getJulDay(gmtJulDay_plusHalfDay , gmt.toLocalTime());
//    int year = gmt.get(YEAR_OF_ERA);
//    boolean isAd = gmt.toLocalDate().getEra() == IsoEra.CE;
//    double sec = gmt.getSecond() + gmt.getNano() / 1_000_000_000.0;
//    return getGmtJulDay(isAd , true , year , gmt.getMonthValue() , gmt.getDayOfMonth() , gmt.getHour() , gmt.getMinute() , sec);
  }

  /**
   * @param gmt proleptic Julian (包含 0 year)
   *                       getLong(JULIAN_DAY)   真正需要的值（左邊減 0.5）
   *  | ISO date          |  Julian Day Number | Astronomical Julian Day |
   *  | 1970-01-01T00:00  |         2,440,588  |         2,440,587.5     |
   */
  public static double getGmtJulDay(JulianDateTime gmt) {
    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 . 因此最後需要減去 0.5
    long gmtJulDay_plusHalfDay = gmt.toLocalDate().getLong(JULIAN_DAY);

    return inner_getJulDay(gmtJulDay_plusHalfDay , gmt.toLocalTime());
  }

  private static double inner_getJulDay(double gmtJulDay_plusHalfDay , LocalTime localTime) {
    int hour = localTime.getHour();
    int min = localTime.getMinute();
    int sec = localTime.getSecond();
    int nano = localTime.getNano();
    double dayValue = hour/24.0 + min/1440.0 + sec / 86400.0 + nano/(1_000_000_000.0 * 86400);

    return gmtJulDay_plusHalfDay + dayValue - 0.5;
  }

  public static double getGmtJulDay(boolean isAd , boolean isGregorian , int year , int month , int day , int hour , int minute , double second) {
    double thisHour = hour + ((double)minute) / 60 +  second / 3600;
    double jd;
    double u, u0, u1, u2;

    u = getNormalizedYear(isAd , year);

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
  public static Pair<Long , Long> splitSecond(double seconds) {
    long secs = (long) seconds;
    long nano = (long) ((seconds - secs)* 1_000_000_000);
    return Pair.of(secs , nano);
  }

  
  @Override
  public int hashCode()
  {
    long SecondBits = Double.doubleToLongBits(second);
    int SecondCode = (int)(SecondBits ^ (SecondBits >>> 32));
    
    int hash=17;
    hash = hash * 31 + (ad ? 1 :0 );
    hash = hash * 31 + year;
    hash = hash * 31 + month;
    hash = hash * 31 + day;
    hash = hash * 31 + hour;
    hash = hash * 31 + minute;
    hash = hash * 31 + SecondCode;
    hash = hash * 31 + (gregorian ? 1 : 0);
    return hash;
  }
  
  //////////////////////////////////////////////////////////////////////
  // Erzeugt aus einem jd/calType Jahr, Monat, Tag und Stunde.        //
  // It does NOT change any global variables.                         //
  //////////////////////////////////////////////////////////////////////
  @NotNull
  private static synchronized IDate swe_revjul (double jd, boolean calType)
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



  public void setYear(int year)
  {
    this.year = year;
  }


  public void setMonth(int month) {
    this.month = month;
    if (this.month > 12 || this.month <= 0)
      normalize();
  }

  public void setDay(int day) {
    this.day = day;
    normalize();
  }

  public void setHour(int hour) {
    this.hour = hour;
    if (this.hour >= 60 || this.hour < 0)
      normalize();
  }

  public void setMinute(int minute) {
    this.minute = minute;
    if (this.minute >= 60 || this.minute < 0)
      normalize();
  }

  public void setSecond(double second) {
    this.second = second;
    if (this.second >= 60 || this.second < 0)
      normalize();
  }

  /** 是否是西元前 , 西元前 傳回 true ; 西元後 傳回 false */
  public boolean isBeforeChrist()
  {
    return !ad;
  }

  public void setBeforeChrist(boolean beforeChrist)
  {
    this.ad = !beforeChrist;
  }

  /** 是否是西元後 , 西元後傳回 true , 西元前傳回 false */
  @Override
  public boolean isAd()
  {
    return ad;
  }

  public void setAd(boolean ad)
  {
    this.ad = ad;
  }


}

class IDate implements java.io.Serializable
{
  public int year;
  public int month;
  public int day;
  public double hour;
}
