/**
 * Created on 2002/8/16 at 下午 06:11:28
 * 2007/05/25 解決了 Gregorian/Julian Calendar 自動轉換的問題, 設定 1582/10/4 之後跳到 1582/10/15
 */
package destiny.core.calendar;

import destiny.tools.AlignUtil;
import destiny.tools.LocaleStringIF;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 代表 『時間』 的物件
 * 1582/10/4 之後跳到 1582/10/15 , 之前是 Julian Calendar , 之後是 Gregorian Calendar
 */
public class Time implements Serializable , LocaleStringIF , DateIF , HmsIF
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
   * 內定的 constructor , 設為現在的系統時間 */
  public Time() {
    this.ad = true;
    LocalDateTime ldt = LocalDateTime.now();
    this.year = ldt.getYear();
    this.month = ldt.getMonthValue();
    this.day = ldt.getDayOfMonth();
    this.hour = ldt.getHour();
    this.minute = ldt.getMinute();
    this.second = ldt.getSecond();
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


  public Time(int year, int month, int day, int hour, int minute, double second) {
    this(year > 0 ,
      (year <=0 ? (-(year-1)) : year) ,
      month , day , hour , minute , second);
  }

  public Time(int year, int month, int day) {
    this(year, month, day, 0, 0, 0);
  }
  
  public Time(int year , int month , int day , double doubleHour) {
    if (year <= 0) {
      this.ad = false;
      this.year = -(year - 1); //取正值
    }
    else {
      this.ad = true;
      this.year = year;
    }
    this.month = month;
    this.day = day;
    checkDate();
    this.hour = (int) doubleHour;
    this.minute = (int) ((doubleHour - this.hour)*60)  ;
    this.second = (doubleHour-this.hour)*3600 - this.minute*60;
    this.normalize();

    int h = (int) doubleHour;
    int m = (int) ((doubleHour - h)*60)  ;
    double s = (doubleHour-h)*3600 - m*60;
  }
  
  
  /** 
   * 利用一個字串 's' 來建立整個時間 , 格式如下： 
   * 0123456789A1234567
   * +YYYYMMDDHHMMSS.SS
   * */
  public Time(@NotNull String s) {
    char ad = s.charAt(0);
    if (ad == '+')
      this.ad = true;
    else if (ad == '-')
      this.ad = false;
    else
      throw new RuntimeException("AD not correct : " + ad);

    this.year = Integer.valueOf(s.substring(1, 5).trim());
    this.month = Integer.valueOf(s.substring(5, 7).trim());
    this.day = Integer.valueOf(s.substring(7, 9).trim());
    this.hour = Integer.valueOf(s.substring(9, 11).trim());
    this.minute = Integer.valueOf(s.substring(11, 13).trim());
    this.second = Double.valueOf(s.substring(13));
    checkDate();
  }
  
  /** 傳回最精簡的文字表示法 , 可以餵進去 {@link #Time(String)} 裡面*/
  @NotNull
  public String getDebugString() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.ad ? '+' : '-');
    sb.append(AlignUtil.alignRight(this.year, 4, ' '));
    sb.append(AlignUtil.alignRight(this.month, 2, ' '));
    sb.append(AlignUtil.alignRight(this.day, 2, ' '));
    sb.append(AlignUtil.alignRight(this.hour, 2, ' '));
    sb.append(AlignUtil.alignRight(this.minute, 2, ' '));
    sb.append(this.second);
    return sb.toString();
  }

  /** 
   * 取得此時間的 timestamp
   * TODO : 要確認 1582 之前是否正常 
   * */
  @NotNull
  public Timestamp getTimestamp() {
    Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, (int) second);
    return new Timestamp(cal.getTimeInMillis());
  }
  
  /**
   * 從 Timestamp 取得 Time 物件
   * TODO : 要確認 1582 之前是否正常
   */
  @NotNull
  public static Time getTime(@NotNull Timestamp ts) {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(ts.getTime());
    return new Time(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
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
   * 建立一個和原本 Time 相差 diffSeconds 秒的時間
   */
  public Time(@NotNull Time originTime , double diffSeconds) {
    double oldJulDay = originTime.getGmtJulDay();
    /** TODO : 解決 Round-off error 的問題 */
    double newJulDay = originTime.getGmtJulDay() + diffSeconds/(24*60*60);
    if ((oldJulDay >= GREGORIAN_START_JULIAN_DAY && newJulDay < GREGORIAN_START_JULIAN_DAY) ||  //往前數，跨過了1582/10/15 之前
        ( oldJulDay < GREGORIAN_START_JULIAN_DAY && newJulDay >= GREGORIAN_START_JULIAN_DAY))    //往後數，超越了1582/10/4子夜之後
    {
      Time time2 = new Time(newJulDay);
      this.gregorian = time2.isGregorian();
      this.ad = time2.isAd();
      this.year = time2.getYear();
      this.month = time2.getMonth();
      this.day = time2.getDay();
      this.hour = time2.getHour();
      this.minute = time2.getMinute();
      this.second = time2.getSecond();
    }
    else {
      this.gregorian = originTime.isGregorian();
      this.ad = originTime.isAd();
      this.year = originTime.getYear();
      this.month = originTime.getMonth();
      this.day = originTime.getDay();
      this.hour = originTime.getHour();
      this.minute = originTime.getMinute();
      this.second = originTime.getSecond() + diffSeconds;
      this.normalize();
    }
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

  /** 加上幾秒 , 傳回新的 time 物件 */
  public Time addSeconds(double value) {
    return new Time(this , value);
  }
    
  public LocalDateTime toLocalDateTime() {

    if (year > 1582) {
      return LocalDateTime.of(year , month , day , hour , minute , (int) second);
    } else {
      int y = (ad ? year : year+1);
      // TODO : Java8 的 LocalDate 並非 GregorianCalendar . 是 "proleptic" Gregorian Calendar . 不考慮 cutover
      // 要考慮是否要在此作轉換
      return LocalDateTime.of(y , month , day , hour , minute , (int) second);
    }
  }

  /**
   * TODO : 檢查 1582 的轉換
   */
  public static Time from(LocalDateTime ldt) {
    return new Time(ldt.getYear() > 1 , ldt.getYear() , ldt.getMonthValue() , ldt.getDayOfMonth() , ldt.getHour() , ldt.getMinute() , ldt.getSecond());
  }
  
  
  /** 取得西元年份，注意，這裡的傳回值不可能小於等於0 */
  @Override
  public int getYear() { return this.year; }
  
  /**
   * 取得不中斷的年份
   * 西元前1年, 傳回 0
   * 西元前2年, 傳回 -1
   */
  private int getNormalizedYear() {
    return getNormalizedYear(ad, year);
  }

  public static int getNormalizedYear(boolean ad , int year) {
    if (!ad)
      return -(year-1);
    else
      return year;
  }

  @Override
  public int getMonth() { return this.month; }
  
  @Override
  public int getDay() { return this.day; }
  @Override
  public int getHour() { return this.hour; }
  @Override
  public int getMinute() { return this.minute; }
  @Override
  public double getSecond() { return this.second; }
  public boolean isGregorian() { return gregorian; }
  
  public boolean isBefore(@NotNull Time targetTime)
  {
    // 時間的比對前後順序不需考慮是否真是 GMT , 兩個同時區的 LMT 以 GMT 抓出 Julian Day 仍可比對先後順序 
    //return this.getGmtJulDay() < targetTime.getGmtJulDay() ? true : false;
    return this.getGmtJulDay() < targetTime.getGmtJulDay();
  }

  public boolean isAfter(@NotNull Time targetTime)
  {
    //return this.getGmtJulDay() > targetTime.getGmtJulDay() ? true : false;
    return this.getGmtJulDay() > targetTime.getGmtJulDay();
  }

  /** 目前此 time 是否介於 t1 與 t2 中間 */
  public boolean isBetween(Time t1 , Time t2) {
    double d = getGmtJulDay();
    double d1 = t1.getGmtJulDay();
    double d2 = t2.getGmtJulDay();
    return
      (d2 > d1 && d > d1 && d2 > d) ||
      (d1 > d2 && d > d2 && d1 > d);
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
  

  /** 從 LMT 轉換到 GMT 
   * 2012/3/4 新增檢查 : loc 是否定義了 minuteOffset (優先權高於 timezone)
   * */
  @NotNull
  public static Time getGMTfromLMT(@NotNull Time lmt , @NotNull Location loc)
  {
    if (loc.isMinuteOffsetSet()) {
      return new Time(lmt, 0 - loc.getMinuteOffset() * 60);
    }
    else {
      TimeZone localZone = loc.getTimeZone();
      GregorianCalendar cal = new GregorianCalendar(localZone);

      cal.set(lmt.getYear(), lmt.getMonth() - 1, lmt.getDay(), lmt.getHour(), lmt.getMinute(), (int) lmt.getSecond());
      double secondsOffset = localZone.getOffset(cal.getTimeInMillis()) / 1000;
      return new Time(lmt, 0 - secondsOffset);

      //LocalDateTime ldt = LocalDateTime.of(lmt.year , lmt.month , lmt.day , lmt.hour , lmt.minute , (int) lmt.second);
    }
  }

  /** 從 GMT 轉換成 LMT
   * 2012/3/4 新增檢查 : loc 是否定義了 minuteOffset (優先權高於 timezone)
   *  */
  @NotNull
  public static Time getLMTfromGMT(@NotNull Time gmt , @NotNull Location loc)
  {
    if (loc.isMinuteOffsetSet())
    {
      return new Time(gmt , loc.getMinuteOffset()*60);
    }
    else
    {
      TimeZone gmtZone = TimeZone.getTimeZone("GMT");
      GregorianCalendar cal = new GregorianCalendar(gmtZone);

      cal.set(gmt.getYear(), gmt.getMonth()-1 , gmt.getDay() , gmt.getHour() , gmt.getMinute() , (int)gmt.getSecond());
      
      TimeZone localTz = loc.getTimeZone();
      double secondsOffset = localTz.getOffset(cal.getTimeInMillis()) / 1000;
      
      return new Time(gmt , secondsOffset);      
    }
  }
  
  @Override
  public String toString()
  {
    return TimeDecorator.getOutputString(this, Locale.getDefault());
  }
  
  public String toString(Locale locale)
  {
    return TimeDecorator.getOutputString(this, locale);
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
   */
  public static double getGmtJulDay(LocalDateTime gmt) {
    int year = gmt.getYear() > 0 ? gmt.getYear() : -(gmt.getYear()-1);
    boolean isAd = gmt.getYear() > 0;
    return getGmtJulDay(isAd , true , year , gmt.getMonthValue() , gmt.getDayOfMonth() , gmt.getHour() , gmt.getMinute() , gmt.getSecond());
  }

  /**
   * @param gmt proleptic Julian (包含 0 year)
   */
  public static double getGmtJulDay(JulianDateTime gmt) {
    int year = gmt.getYear();
    boolean isAd = gmt.getProlepticYear() > 0;
    return getGmtJulDay(isAd , false , year , gmt.getMonth() , gmt.getDayOfMonth() ,
      gmt.getHour() , gmt.getMinute() , gmt.getSecond());
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

  /**
   * 取得目前時刻和目標時刻相差幾秒
   * @param target 目標時刻
   * @return 相差秒數，如果目標時刻早於 (prior to) 目前時刻，傳回正值。否則傳回負值
   */
  public double diffSeconds(@NotNull Time target) {
    double diffDays = this.getGmtJulDay() - target.getGmtJulDay();
    return diffDays * 86400; //24*60*60
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
