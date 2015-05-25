/** 2009/10/20 下午10:53:51 by smallufo */
package destiny.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertNotEquals;

public class TimezoneTest
{
  @Test
  public void testTimeZone()
  {
    TimeZone tp = TimeZone.getTimeZone("Asia/Taipei");
    TimeZone sh = TimeZone.getTimeZone("Asia/Shanghai");
    
    assertNotEquals(tp, sh);
  }

  @Test
  public void testGetAvailableIds()
  {
    for(String tz : TimeZone.getAvailableIDs(8*60*60*1000))
    {
      TimeZone timezone = TimeZone.getTimeZone(tz);
      System.out.println(tz + " = " + timezone.getDisplayName(false , TimeZone.SHORT , Locale.ENGLISH) + " , " +
          timezone.getDisplayName(false , TimeZone.LONG , Locale.ENGLISH) + " , " + 
          timezone.getDisplayName(Locale.ENGLISH));
    }    
  }

  @Test
  public void testSingapore()
  {
    TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
    System.out.println(tz);
    
    GregorianCalendar cal;
    
    cal = new GregorianCalendar(1981, 12-1 , 31 , 23 , 00);
    System.out.println("(1) 1981/12/31  23:00 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    cal = new GregorianCalendar(1981, 12-1 , 31 , 23 , 59);
    System.out.println("(2) 1981/12/31  23:59 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    cal = new GregorianCalendar(1982, 1-1 , 1 , 0 , 0);
    System.out.println("(3) 1982/01/01  00:00 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    cal = new GregorianCalendar(1982, 1-1 , 1 , 0 , 29 , 59);
    System.out.println("(4) 1982/01/01  00:29 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    cal = new GregorianCalendar(1982, 1-1 , 1 , 0 , 30 , 0);
    System.out.println("(5) 1982/01/01  00:30 : DST ? " + tz.inDaylightTime(cal.getTime()) + ", " + tz.getOffset(cal.getTimeInMillis()) + " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    
    cal = new GregorianCalendar();
    cal.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
    cal.setTimeInMillis(378664200000l);
    System.out.println(cal.getTime());
    
    cal.setTimeInMillis(378664200000l-1000);
    System.out.println(cal.getTime());
    
    

  }

  @Test
  public void testTaiwan()
  {
    TimeZone tp = TimeZone.getTimeZone("Asia/Taipei");
    System.out.println(tp);
    
    GregorianCalendar cal;
    
    cal = new GregorianCalendar(1945 , 4-1 , 30 , 23 , 59);
    System.out.println("(1) 1945/04/30  23:59 : DST ? " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    //根據資料：台灣於 1945/5/1 凌晨 0 時進入 DST , 撥快一小時
    cal = new GregorianCalendar(1945 , 5-1 , 1 , 0 , 0);
    System.out.println("(2) 1945/05/01  00:00 : DST ? " + tp.inDaylightTime(cal.getTime()) + " , " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    cal = new GregorianCalendar(1945 , 5-1 , 1 , 0 , 1);
    System.out.println("(3) 1945/05/01  00:01 : DST ? " + tp.inDaylightTime(cal.getTime()) + " , " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());

    //根據資料：台灣於 1945/10/1 凌晨 0 時離開 DST , 結束日光節約時間
    cal = new GregorianCalendar(1945 , 9-1 , 30 , 22 , 59);
    System.out.println("(4) 1945/09/30  22:59 : DST ? " + tp.inDaylightTime(cal.getTime()) + " , " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    //注意，這裡要小心！有點詭異。為什麼晚上 23 點就結束 DST ??
    
    cal = new GregorianCalendar(1945 , 9-1 , 30 , 23 , 00);
    System.out.println("(5) 1945/09/30  23:00 : DST ? " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    cal = new GregorianCalendar(1945 , 10-1 , 1 , 0 , 0);
    System.out.println("(6) 1945/10/01  00:00 : DST ? " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ " , date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());    
  }
  
  /**  
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   * */
  @Test
  public void testTaiwan63()
  {
    TimeZone tp = TimeZone.getTimeZone("Asia/Taipei");
    System.out.println(tp);
    TimeZone gmt = TimeZone.getTimeZone("GMT");
    
    GregorianCalendar cal;
    
    
    cal = new GregorianCalendar(gmt , Locale.UK);
    
    
    System.out.println(cal);
    System.out.println(gmt.getOffset(Calendar.ZONE_OFFSET));
    System.out.println(tp .getOffset(Calendar.ZONE_OFFSET));
    
    System.out.println("日光節約時間前");
    cal.set(1974, 3-1, 31, 15, 59 , 59);
    System.out.println(cal.getTime() + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    System.out.println("開始日光節約時間");
    cal.set(1974 , 3-1 , 31 , 16 , 0 , 0);
    System.out.println("hour = " + cal.get(Calendar.HOUR_OF_DAY));
    System.out.println(cal + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    //日光節約時間中
    cal.set(1974 , 3-1 , 31 , 16 , 1 , 0);
    System.out.println(cal.getTime() + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
    
    cal.set(1974 , 3-1 , 31 , 16 , 59 , 0);
    System.out.println(cal.getTime() + " , " + tp.inDaylightTime(cal.getTime()) + ", " + tp.getOffset(cal.getTimeInMillis()) + " rawOffset = " + tp.getRawOffset()+ ", date = " + cal.getTime() + " , millis = " + cal.getTimeInMillis());
  }

}

