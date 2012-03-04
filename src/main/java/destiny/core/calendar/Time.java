/**
 * Created on 2002/8/16 at 下午 06:11:28
 * 2007/05/25 解決了 Gregorian/Julian Calendar 自動轉換的問題, 設定 1582/10/4 之後跳到 1582/10/15
 */
package destiny.core.calendar;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import destiny.utils.AlignUtil;
import destiny.utils.LocaleStringIF;

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
   * Julian Canendar    終止於西元 1582/10/4  , 該日的 Julian Day 是 2299159.5
   * Gregorian Calendar 開始於西元 1582/10/15 , 該日的 Julian Day 是 2299160.5 
   * */
  private static double gregorianStartJulianDay=2299160.5;
  
  /** 
   * 內定是 Gregorian Calendar <BR/>
   * Gregorian == TRUE <BR/>
   * Julian == FALSE
   * */
  protected boolean gregorian = true ;
  
  /**
   * 內定的 contructor , 設為現在的系統時間 */
  public Time()
  {
    this.ad = true;
    GregorianCalendar cal = new GregorianCalendar();
    this.year = cal.get(Calendar.YEAR);
    this.month = cal.get(Calendar.MONTH) +1;
    this.day = cal.get(Calendar.DAY_OF_MONTH);
    this.hour = cal.get(Calendar.HOUR_OF_DAY);
    this.minute = cal.get(Calendar.MINUTE);
    this.second = cal.get(Calendar.SECOND);
  }
  

  /**
   * TODO : 解決所有 Julian Calendar 的問題
   */
  public Time(boolean isAD , int Year , int Month , int Day , int Hour , int Minute , double Second )
  {
    if (Year <= 0)
      throw new RuntimeException("Year cannot be less than or equal to 0 in this constructor!");
    this.ad = isAD;
    this.year = Year;
    this.month = Month;
    this.day = Day;
    this.hour = Hour;
    this.minute = Minute;
    this.second = Second;
    checkDate();
    this.normalize();
  }
  

  public Time(int Year , int Month , int Day , int Hour , int Minute , double Second)
  {
    if (Year <= 0)
    {
      this.ad = false;
      this.year = -(Year-1); //取正值
    }
    else
    {
      this.ad = true;
      this.year = Year;
    }
    this.month = Month;
    this.day = Day;
    this.hour = Hour;
    this.minute = Minute;
    this.second = Second;
    checkDate();
    this.normalize();
  }
  
  public Time(int Year , int Month , int Day)
  {
    if (Year <= 0)
    {
      this.ad = false;
      this.year = -(Year-1); //取正值
    }
    else
    {
      this.ad = true;
      this.year = Year;
    }
    this.month = Month;
    this.day = Day;
    this.hour = 0;
    this.minute = 0;
    this.second = 0;
    checkDate();
    this.normalize();
    
  }
  
  public Time(int Year , int Month , int Day , double doubleHour)
  {
    if (Year <= 0)
    {
      this.ad = false;
      this.year = -(Year-1); //取正值
    }
    else
    {
      this.ad = true;
      this.year = Year;
    }
    this.month = Month;
    this.day = Day;
    checkDate();
    this.hour = (int) doubleHour;
    this.minute = (int) ((doubleHour - this.hour)*60)  ;
    this.second = (doubleHour-this.hour)*3600 - this.minute*60;
    this.normalize();
  }
  
  
  /** 
   * 利用一個字串 's' 來建立整個時間 , 格式如下： 
   * 0123456789A1234567
   * +YYYYMMDDHHMMSS.SS
   * */
  public Time(String s)
  {
    char ad = s.charAt(0);
    if (ad == '+')
      this.ad = true;
    else if (ad == '-')
      this.ad = false;
    else
      throw new RuntimeException("AD not correct : " + ad);
    
    this.year = Integer.valueOf(s.substring(1, 5).trim()).intValue();
    this.month = Integer.valueOf(s.substring(5,7).trim()).intValue();
    this.day = Integer.valueOf(s.substring(7,9).trim()).intValue();
    this.hour = Integer.valueOf(s.substring(9,11).trim()).intValue();
    this.minute = Integer.valueOf(s.substring(11,13).trim()).intValue();
    this.second = Double.valueOf(s.substring(13));
    checkDate();
  }
  
  /** 傳回最精簡的文字表示法 , 可以餵進去 {@link #Time(String)} 裡面*/
  public String getDebugString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(this.ad ? '+' : '-');
    sb.append(AlignUtil.alignRight(this.year, 4 , ' '));
    sb.append(AlignUtil.alignRight(this.month, 2 , ' '));
    sb.append(AlignUtil.alignRight(this.day, 2 , ' '));
    sb.append(AlignUtil.alignRight(this.hour, 2 , ' '));
    sb.append(AlignUtil.alignRight(this.minute, 2 , ' '));
    sb.append(this.second);
    return sb.toString();
  }

  /** 
   * 取得此時間的 timestamp
   * TODO : 要確認 1582 之前是否正常 
   * */
  public Timestamp getTimestamp()
  {
    Calendar cal = new GregorianCalendar(year, month-1 , day, hour , minute , (int) second);
    return new Timestamp(cal.getTimeInMillis()); 
  }
  
  /**
   * 從 Timestamp 取得 Time 物件
   * TODO : 要確認 1582 之前是否正常
   */
  public static Time getTime(Timestamp ts)
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(ts.getTime());
    return new Time(cal.get(Calendar.YEAR) , cal.get(Calendar.MONTH) +1 , cal.get(Calendar.DAY_OF_MONTH) , cal.get(Calendar.HOUR_OF_DAY) , cal.get(Calendar.MINUTE) , cal.get(Calendar.SECOND));
    
  }
  
  /**
   * 檢查時間是否落入 1582/10/5~14 之間 , 如果有 , 就丟出 Exception
   */
  private void checkDate()
  {
    if (this.isAd() == true && year ==1582 && month==10 && (day>=5 && day<=14))
      throw new RuntimeException("Invalid time! Date between 1582/10/5(inclusive) and 1582/10/14(inclusive) is invalid.");
    if (getGmtJulDay() >= gregorianStartJulianDay )
      this.gregorian = true;
    else
      this.gregorian = false;
  }
  
  public Time(double julianDay)
  {
    if (julianDay >= gregorianStartJulianDay)
      gregorian=true;
    else
      gregorian=false;
    IDate dt=swe_revjul(julianDay, gregorian);
    if (dt.year <= 0)
    {
      this.ad = false;
      this.year = -(dt.year-1); //取正值
    }
    else
      this.year = dt.year;

    this.month=dt.month;
    this.day=dt.day;
    this.hour=(int)dt.hour;
    this.minute = (int) (dt.hour * 60 - hour * 60);
    this.second = dt.hour * 3600 - hour * 3600 - minute * 60;
  }
  
  /**
   * 從 Julian Day 建立 Time
   * @param JulianDay
   * @param isGregorian
   * @deprecated
   */
  @Deprecated
  public Time(double JulianDay , boolean isGregorian)
  {
    this.gregorian = isGregorian;
    IDate dt=swe_revjul(JulianDay, isGregorian);
    if (dt.year <= 0)
    {
      this.ad = false;
      this.year = -(dt.year-1); //取正值
    }
    else
      this.year = dt.year;

    this.month=dt.month;
    this.day=dt.day;
    this.hour=(int)dt.hour;
    this.minute = (int) (dt.hour * 60 - hour * 60);
    this.second = dt.hour * 3600 - hour * 3600 - minute * 60;
  }
  
  /**
   * 建立一個和原本 Time 相差 diffSeconds 秒的時間
   */
  public Time(Time originTime , double diffSeconds)
  {
    double oldJulDay = originTime.getGmtJulDay();
    /** TODO : 解決 Round-off error 的問題 */
    double newJulDay = originTime.getGmtJulDay() + diffSeconds/(24*60*60);
    if ((oldJulDay >= gregorianStartJulianDay && newJulDay < gregorianStartJulianDay ) ||  //往前數，跨過了1582/10/15 之前 
        ( oldJulDay < gregorianStartJulianDay && newJulDay >= gregorianStartJulianDay))    //往後數，超越了1582/10/4子夜之後
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
    else
    {
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
   * @return 是否是西元「後」, 西元前為 false , 西元後為 true (default)
   */
  //public boolean isAD() { return this.AD; }
  
  
  
  /** 取得西元年份，注意，這裡的傳回值不可能小於等於0 */
  @Override
  public int getYear() { return this.year; }
  
  /**
   * 取得不中斷的年份
   * 西元前1年, 傳回 0
   * 西元前2年, 傳回 -1
   */
  public int getNormalizedYear() 
  {
    if (ad == false)
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
  
  public boolean isBefore(Time targetTime)
  {
    // 時間的比對前後順序不需考慮是否真是 GMT , 兩個同時區的 LMT 以 GMT 抓出 Julian Day 仍可比對先後順序 
    return this.getGmtJulDay() < targetTime.getGmtJulDay() ? true : false;
  }
  
  public boolean isAfter(Time targetTime)
  {
    return this.getGmtJulDay() > targetTime.getGmtJulDay() ? true : false;
  }
  
  private void normalize()
  {
    long skips = 0 ; //進位
    if ( second >= 60)
    {
      skips = (long) (second / 60);
      second = second - skips*60;
    }
    else if ( second < 0 )
    {
      skips = (long) (second / 60) -1;
      if (second % 60 ==0)
        skips++;
      second = second - skips*60;
    }
    minute += skips;
    
    skips = 0;
    if ( minute >= 60)
    {
      skips = (minute / 60);
      minute = (int) (minute - skips*60);
    }
    else if (minute < 0)
    {
      skips = (minute / 60) -1;
      if (minute % 60 ==0)
        skips++;
      minute = (int) (minute - skips*60);
    }
    hour += skips;
    
    skips = 0;
    
    if ( hour >= 24 )
    {
      skips = (hour / 24);
      hour = (int) (hour - skips*24);
    }
    else if (hour < 0)
    {
      skips = (hour / 24) -1;
      if (hour % 24 ==0)
        skips++;
      hour = (int) (hour - skips*24);
    }
    double julday = this.getGmtJulDay();
    julday += skips;

    
    Time t2 = new Time(julday , this.gregorian);
    this.day = t2.getDay();
    this.month = t2.getMonth();
    this.year = t2.getYear();
    this.ad = t2.isAd();
    //this.Gregorian = t2.isGregorian();
    if (ad==false)
      this.gregorian = getCalendarType(-(t2.getYear()-1) , t2.getMonth() , t2.getDay() , t2.getHour() , t2.getMinute() , t2.getSecond());
    else
      this.gregorian = getCalendarType(t2.getYear() , t2.getMonth() , t2.getDay() , t2.getHour() , t2.getMinute() , t2.getSecond());
    
  }//normalize

  
  /**
   * 與 Gregorian Calendar 的啟始日比對 , 判斷輸入的日期是否是 Gregorian Calendar
   */
  private boolean getCalendarType(int year, int month, int day, int hour , int minute , double second) 
  {
    IDate dt=swe_revjul(gregorianStartJulianDay,true);
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
  public static Time getGMTfromLMT(Time lmt , Location loc)
  {
    if (loc.isMinuteOffsetSet())
    {
      return new Time(lmt , 0-loc.getMinuteOffset()*60);
    }
    else
    {
      TimeZone localZone = loc.getTimeZone();
      GregorianCalendar cal = new GregorianCalendar(localZone);
      
      cal.set(lmt.getYear() , lmt.getMonth()-1 , lmt.getDay() , lmt.getHour() , lmt.getMinute() , (int)lmt.getSecond());
      double secondsOffset = localZone.getOffset(cal.getTimeInMillis()) / 1000;
      
      return new Time(lmt , 0-secondsOffset);  
    }
  }

  /** 從 GMT 轉換成 LMT
   * 2012/3/4 新增檢查 : loc 是否定義了 minuteOffset (優先權高於 timezone)
   *  */
  public final static Time getLMTfromGMT(Time gmt , Location loc)
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
  public boolean equals(Object o)
  {
    if ((o != null) && (o.getClass().equals(this.getClass())))
    {
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
   * @see swisseph.SweDate
   * @return Julian Day 
   */
  public double getGmtJulDay()
  {
    double thisHour = hour + ((double)minute) / 60 +  second / 3600;

    double jd;
    double u, u0, u1, u2;
    
    u = getNormalizedYear();
    
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
    if (gregorian)
    {
      u2 = Math.floor(Math.abs(u) / 100) - Math.floor(Math.abs(u) / 400);
      if (u < 0.0)
      {
        u2 = -u2;
      }
      jd = jd - u2 + 2;
      if ((u < 0.0) && (u / 100 == Math.floor(u / 100))
          && (u / 400 != Math.floor(u / 400)))
      {
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
  public double diffSeconds(Time target)
  {
    double diffDays = this.getGmtJulDay() - target.getGmtJulDay();
    return diffDays*86400; //24*60*60
  }
  
  @Override
  public int hashCode()
  {
    long SecondBits = Double.doubleToLongBits(second);
    int SecondCode = (int)(SecondBits ^ (SecondBits >>> 32));
    
    int hash=17;
    hash = hash * 31 + (ad == true ? 1 :0 );
    hash = hash * 31 + year;
    hash = hash * 31 + month;
    hash = hash * 31 + day;
    hash = hash * 31 + hour;
    hash = hash * 31 + minute;
    hash = hash * 31 + SecondCode;
    hash = hash * 31 + (gregorian == true ? 1 : 0);
    return hash;
  }
  
  //////////////////////////////////////////////////////////////////////
  // Erzeugt aus einem jd/calType Jahr, Monat, Tag und Stunde.        //
  // It does NOT change any global variables.                         //
  //////////////////////////////////////////////////////////////////////
  private synchronized IDate swe_revjul (double jd, boolean calType) 
  {
    IDate dt=new IDate();
    double u0,u1,u2,u3,u4;

    u0 = jd + 32082.5;
    if (calType == true) 
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
  

  public void setMonth(int month)
  {
    this.month = month;
    if (this.month>12 || this.month <=0)
      normalize();
  }

  public void setDay(int day)
  {
    this.day = day;
    normalize();
  }

  public void setHour(int hour)
  {
    this.hour = hour;
    if (this.hour >=60 || this.hour < 0 )
      normalize();
  }

  public void setMinute(int minute)
  {
    this.minute = minute;
    if (this.minute >=60 || this.minute < 0 )
      normalize();
  }

  public void setSecond(double second)
  {
    this.second = second;
    if (this.second >=60 || this.second < 0 )
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
