/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 01:47:36
 */
package destiny.core.calendar;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class TimeTest
{
  private Logger logger = LoggerFactory.getLogger(getClass());

  private Time time;
  private Time origin;
  private Time actual ;
  private Time expected ;

  @Test
  public void testTime() {
    Time t = new Time();
    t.setSecond(12.1234);
    String s = String.format("%02d月%02d日 %02d時 %02d分 ", t.getMonth(), t.getDay(), t.getHour(), t.getMinute())
      + StringUtils.leftPad(String.format("%3.2f秒", t.getSecond()), 6, '0');
    System.out.println("time = " + s);
  }

  @Test
  public void testDateEquals() {
    Time t1 = new Time();
    Time t2 = new Time(t1 , 100.0);

    LocalDate ld1 = t1.toLocalDateTime().toLocalDate();
    LocalDate ld2 = t2.toLocalDateTime().toLocalDate();

    logger.info("ld1 same ld2 ? {} . ld1 equals ld2 ? {} . Objects.equals ? {}" , ld1.isEqual(ld2) , ld1.equals(ld2) , Objects.equals(ld1 , ld2));
  }

  /**  
   * 已知：
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  public void testGetGMTfromLMT()
  {
    Location loc = new Location(121.30 , 25.03 , TimeZone.getTimeZone("Asia/Taipei"));
    Time gmt,lmt;
    
    //日光節約時間前一秒
    lmt = new Time(1974,3,31 , 23 ,  59 , 59);
    gmt = Time.getGMTfromLMT(lmt, loc);
    assertEquals(new Time(1974,3,31,15,59,59) , gmt);
    
    //開始日光節約時間，時間調快一小時 , 變成 GMT+9
    lmt = new Time(1974,4,1 , 0,0,0); //其實是沒有 0:00~1:00 這段時間的
    gmt = Time.getGMTfromLMT(lmt, loc);
    assertEquals(new Time(1974,3,31,15,0,0) , gmt); //所以 GMT 時間會「回溯」一小時。
    
    //真正日光節約時間，開始於 1:00AM
    lmt = new Time(1974,4,1 , 1,0,0); 
    gmt = Time.getGMTfromLMT(lmt, loc);
    assertEquals(new Time(1974,3,31,16,0,0) , gmt); //真正銜接到「日光節約時間」前一秒
    
    
    //日光節約時間結束前一秒 , GMT+9
    lmt = new Time(1974,9,30 , 22,59,59);
    gmt = Time.getGMTfromLMT(lmt, loc);
    assertEquals(new Time(1974,9,30 , 13,59,59) , gmt);
    
    lmt = Time.getLMTfromGMT(new Time(1974,9,30,14,0,0), loc);
    System.err.println(lmt); //推估當時可能過了兩次 23:00-24:00 的時間，以調節和 GMT 的時差
    
    //正式結束日光節約時間 , GMT+8
    lmt = new Time(1974,9,30 , 23,0,0);
    gmt = Time.getGMTfromLMT(lmt, loc);
    assertEquals(new Time(1974,9,30 , 15,0,0) , gmt);
    
    //GMT+8
    lmt = new Time(1974,9,30 , 23,59,59);
    gmt = Time.getGMTfromLMT(lmt, loc);
    assertEquals(new Time(1974,9,30 , 15,59,59) , gmt);
    
    //GMT+8
    lmt = new Time(1974,10,1 , 0,0,0);
    gmt = Time.getGMTfromLMT(lmt, loc);
    assertEquals(new Time(1974,9,30 , 16,0,0) , gmt);
    
  }
  
  /**  
   * 已知：
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  public void testGetLMTfromGMT()
  {
    Location loc = new Location(121.30 , 25.03 , TimeZone.getTimeZone("Asia/Taipei"));
    Time gmt,lmt;
    
    //日光節約時間前一秒
    gmt = new Time(1974,3,31 , 15,  59 , 0);
    lmt = Time.getLMTfromGMT(gmt, loc);
    System.out.println(lmt);
    assertEquals(new Time(1974,3,31,23,59,0) , lmt);
    
    //開始日光節約時間
    gmt = new Time(1974,3,31 , 16,  0 , 0);
    lmt = Time.getLMTfromGMT(gmt, loc);
    System.out.println(lmt);
    assertEquals(new Time(1974,4,1,1,0,0) , lmt); //跳躍一小時
    
    //日光節約時間結束前一秒
    gmt = new Time(1974,9,30 , 14 , 59 , 59);
    lmt = Time.getLMTfromGMT(gmt, loc);
    System.out.println(lmt);
    assertEquals(new Time(1974,9,30,23,59,59), lmt); //與下面重複
    
    //日光節約時間結束前一秒
    gmt = new Time(1974,9,30 , 15 , 59 , 59);
    lmt = Time.getLMTfromGMT(gmt, loc);
    System.out.println(lmt);
    assertEquals(new Time(1974,9,30,23,59,59), lmt); //與上面重複
    
    //日光節約時間結束
    gmt = new Time(1974,9,30 , 16 , 0 , 0);
    lmt = Time.getLMTfromGMT(gmt, loc);
    System.out.println(lmt);
    assertEquals(new Time(1974,10,1,0,0,0) , lmt);
  }
  
  /** 測試從 "+20000102000000.00" 建立時間 */
  @Test
  public void testTimeFromString()
  {
    actual = new Time("+20000203040506.789");
    expected = new Time(true , 2000 , 2 , 3 , 4 , 5 , 6.789);
    assertEquals(expected , actual);
    
    actual = new Time("-20010203040506.789");
    expected = new Time(false , 2001 , 2 , 3 , 4 , 5 , 6.789);
    assertEquals(expected , actual);
  }

  @Test
  public void testTimeDebugString()
  {
    expected = new Time(true , 2001 , 2 , 3 , 4 , 5 , 6.789);
    actual = new Time(expected.getDebugString());
    assertEquals(expected , actual);
    
    expected = new Time(true , 1 , 2 , 3 , 4 , 5 , 6.789);
    actual = new Time(expected.getDebugString());
    assertEquals(expected , actual);
  }
  
  /**
   * 1582/10/4 之後跳到 1582/10/15 , 之前是 Julian Calendar , 之後是 Gregorian Calendar
   * 測試 10/5~10/14 之間的錯誤日期 
   */
  @Test
  public void testInvalidTime()
  {
    try
    {
      origin = new Time(1582 , 10 , 5 , 0 , 0 , 0);
      fail("Should throw new RuntimeExcepton");
    }
    catch(RuntimeException expected)
    {
      assertTrue(true);
    }
    
    try
    {
      origin = new Time(1582 , 10 , 14 , 0 , 0 , 0);
      fail("Should throw new RuntimeExcepton");
    }
    catch(RuntimeException expected)
    {
      assertTrue(true);
    }
  }
  
  /**
   * 測試由 Time 的 diff Seconds 再建立一個 Time
   */
  @Test
  public void testTimeDiffSeconds1()
  {
    /** 西元兩千年 */
    origin = new Time(2000 , 1, 1 , 0 , 0 , 0);
    
    actual = new Time(origin , 1);
    expected = new Time(2000, 1, 1 , 0 , 0 , 1);
    assertEquals(expected , actual);
    
    actual = new Time(origin , -1);
    expected = new Time(1999, 12, 31 , 23 , 59 , 59);
    assertEquals(expected , actual);
    
    /** 西元元年 1/1 */
    origin = new Time(true , 1 , 1 , 1, 0 , 0 , 0);
    actual = new Time(origin , 1);
    expected = new Time(true, 1 , 1 , 1 , 0 , 0 , 1);
    assertEquals(expected , actual);
    /** 減去一秒 , 變成西元前一年 , 12/31 23:59:59 */
    actual = new Time(origin , -1);
    expected = new Time(false, 1 , 12 , 31 , 23 , 59 , 59);
    assertEquals(expected , actual);
  }
  
  /** 測試 1582/10/4 --- 1582/10/15 的日期轉換 , 以「日」為單位來 diff , 先避掉 Round-off error */
  @Test
  public void testTimeDiffSeconds2()
  {
    origin = new Time(1582,10,15,0,0,0);
    actual = new Time(origin , -1*24*60*60);
    expected= new Time(1582,10,4,0,0,0);
    assertEquals(expected , actual);
    
    origin = new Time(1582,10,4,0,0,0);
    actual = new Time(origin , 1*24*60*60);
    expected= new Time(1582,10,15,0,0,0);
    assertEquals(expected , actual);
  }
  
  /** 測試 1582/10/15 子初的日期轉換 , 以「秒」為單位來 diff , 要能解決 Round-off error */
  @Test
  public void testTimeDiffSeconds3()
  {
    origin = new Time(1582,10,15,0,0,0);
    actual = new Time(origin , 1); //先往後加一秒，不會碰到切換點
    expected= new Time(1582,10,15,0,0,1);
    assertEquals(expected , actual);
    
    origin = new Time(1582,10,15,0,0,0);
    actual = new Time(origin , -1); //往前加一秒，會碰到 Gregorian/Julian 切換點
    expected= new Time(1582,10,4,23,59,59);
    assertEquals(expected.getGmtJulDay() , actual.getGmtJulDay() , 0.01);
  }
  
  /** 測試 1582/10/4 子夜 的日期轉換 , 以「秒」為單位來 diff , 要能解決 Round-off error */
  @Test
  public void testTimeDiffSeconds4()
  {
    origin = new Time(1582,10,4,23,59,59);
    actual = new Time(origin , -1); //先往前減一秒，不會碰到切換點
    expected= new Time(1582,10,4,23,59,58);
    assertEquals(expected , actual);
    
    origin = new Time(1582,10,4,23,59,59);
    actual = new Time(origin , 1); //往後加一秒，會碰到 Gregorian/Julian 切換點
    expected= new Time(1582,10,15,0,0,0);
    assertEquals(expected , actual);
  }
  
  /**
   * 測試由 Julian Day 建立 Time 
   */
  @Test
  public void testTimeFromJulianDay()
  {
    //曆法交界日期
    /** 測試 1582/10/4 --- 1582/10/15 的日期轉換 */
    actual = new Time(2299160.5 );
    expected = new Time(1582,10,15,0,0,0);
    assertEquals(expected , actual);
    /** JD 往前一天 , 跳到 1582/10/4 */
    actual = new Time(2299159.5 );
    expected = new Time(1582,10,4,0,0,0);
    assertEquals(expected , actual);
    
    
    actual = new Time(2453330);
    expected = new Time(2004, 11,20 , 12 , 0 , 0);
    assertEquals(expected , actual);
    
    actual = new Time(2452549);
    expected = new Time(2002, 10, 1 , 12 , 0 , 0);
    assertEquals(expected , actual);
    
    //西元元年一月一號
    actual = new Time(1721423.5);
    expected = new Time(true , 1 , 1 , 1 , 0 , 0 , 0);
    assertEquals(expected , actual);
    
    //西元前一年十二月三十一號
    actual = new Time(1721422.5);
    expected = new Time(false , 1 , 12 , 31 , 0 , 0 , 0);
    assertEquals(expected , actual);
    
    //西元前一年一月一號
    actual = new Time(1721057.5);
    expected = new Time(false , 1 , 1 , 1 , 0 , 0 , 0);
    assertEquals(expected , actual);
    
    //西元前二年十二月三十一號
    actual = new Time(1721056.5);
    expected = new Time(false , 2 , 12 , 31 , 0 , 0 , 0);
    assertEquals(expected , actual);
  }
  
  /**
   * 測試 Time.normalize() 進位（退位）是否正確
   **/
  @Test
  public void testNormalize()
  {
    Time t1 , t2;
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 60);
    t2 = new Time(2004 , 11 , 20 , 2 , 31 ,  0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 120);
    t2 = new Time(2004 , 11 , 20 , 2 , 32 ,  0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -1 );
    t2 = new Time(2004 , 11 , 20 , 2 , 29 ,  59);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -59 );
    t2 = new Time(2004 , 11 , 20 , 2 , 29 ,  1);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -61 );
    t2 = new Time(2004 , 11 , 20 , 2 , 28 ,  59);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -60 );
    t2 = new Time(2004 , 11 , 20 , 2 , 29 ,  0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -61 );
    t2 = new Time(2004 , 11 , 20 , 2 , 28 ,  59 );
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -120);
    t2 = new Time(2004 , 11 , 20 , 2 , 28 ,  0  );
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -1800);
    t2 = new Time(2004 , 11 , 20 , 2 ,  0 ,  0   );
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , -1801);
    t2 = new Time(2004 , 11 , 20 , 1 , 59 ,  59   );
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 60);
    t2 = new Time(2004 , 11 , 20 , 2 , 31 ,  0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 61);
    t2 = new Time(2004 , 11 , 20 , 2 , 31 ,  1);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 119);
    t2 = new Time(2004 , 11 , 20 , 2 , 31 ,  59);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 120);
    t2 = new Time(2004 , 11 , 20 , 2 , 32 ,   0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 1800);
    t2 = new Time(2004 , 11 , 20 , 3 ,  0 ,    0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 30 , 1801);
    t2 = new Time(2004 , 11 , 20 , 3 ,  0 ,    1);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 59 , 60);
    t2 = new Time(2004 , 11 , 20 , 3 ,  0 ,  0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 59 , 120);
    t2 = new Time(2004 , 11 , 20 , 3 ,  1 ,   0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 60 , 60);
    t2 = new Time(2004 , 11 , 20 , 3 ,  1 ,  0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 60 , 3600);
    t2 = new Time(2004 , 11 , 20 , 4 ,  0 ,    0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 60 , -3600); //加一小時，減一小時
    t2 = new Time(2004 , 11 , 20 , 2 ,  0 ,    0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 60 , -7200); //加一小時，減兩小時
    t2 = new Time(2004 , 11 , 20 , 1 ,  0 ,    0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 2 , 60 , -7201); //加一小時，減兩小時又一秒
    t2 = new Time(2004 , 11 , 20 , 0 , 59 ,    59);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 24 , 0 , 0);
    t2 = new Time(2004 , 11 , 21 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 25 , 0 , 0);
    t2 = new Time(2004 , 11 , 21 ,  1 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , 48 , 0 , 0);
    t2 = new Time(2004 , 11 , 22 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 ,  -1 , 0 , 0);
    t2 = new Time(2004 , 11 , 19 ,  23 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , -13 , 0 , 0);
    t2 = new Time(2004 , 11 , 19 ,  11 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , -24 , 0 , 0);
    t2 = new Time(2004 , 11 , 19 ,   0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , -25 , 0 , 0);
    t2 = new Time(2004 , 11 , 18 ,  23 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 20 , -25 , 0 , 0);
    t2 = new Time(2004 , 11 , 18 ,  23 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 30 ,  24 , 0 , 0);
    t2 = new Time(2004 , 12 ,  1 ,   0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 30 ,  0 , 0 , 24*60*60);
    t2 = new Time(2004 , 12 ,  1 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 30 ,  0 , 0 , 24*60*60*31);
    t2 = new Time(2004 , 12 , 31 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 , 11 , 30 ,  0 , 0 , 24*60*60*32);
    t2 = new Time(2005 ,  1 ,  1 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2 ,  1 ,  0  ,  0 , -60);
    t2 = new Time(2004 ,  1 , 31 ,  23 , 59 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2 ,  1 ,  0 , 0 , -60*60*24);
    t2 = new Time(2004 ,  1 , 31 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2 ,  1 ,  0 , 0 , -60*60*24*31);
    t2 = new Time(2004 ,  1 ,  1 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2  ,   1 ,  0 ,  0 , -60*60*24*31-1);
    t2 = new Time(2003 ,  12 ,  31 , 23 , 59 , 59);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2 ,  1 ,  0 , 0 , 60*60*24);
    t2 = new Time(2004 ,  2 ,  2 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2 ,  1 ,   0 ,  0 , 60*60*24-1);
    t2 = new Time(2004 ,  2 ,  1 ,  23 , 59 , 59);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2 ,   1 ,  0 , 0 , 60*60*24*28);
    t2 = new Time(2004 ,  2 ,  29 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
    
    t1 = new Time(2004 ,  2 ,   1 ,  0 , 0 , 60*60*24*29);
    t2 = new Time(2004 ,  3 ,   1 ,  0 , 0 , 0);
    assertEquals(t2 , t1);
  }
  
  /**
   * 測試 Julian Day 是否正確
   */
  @Test
  public void testGetGmtJulDay()
  {
    double actual;
    double expected;
    
    time = new Time(false , 4713 , 1 , 1 , 12 , 0 , 0);
    actual = time.getGmtJulDay();
    expected = 0;
    assertEquals(expected , actual , 0);
    
    time = new Time(true , 1900 , 1 , 1, 0 , 0 , 0);
    actual = time.getGmtJulDay();
    expected = 2415020.5;
    assertEquals(expected , actual , 0);
    
    time = new Time(2004, 11,20 , 12 , 0 , 0);
    actual = time.getGmtJulDay();
    expected = 2453330;
    assertEquals(expected , actual , 0);
    
    time = new Time(2004, 11,21 , 0 , 0 , 0);
    actual = time.getGmtJulDay();
    expected = 2453330.5;
    assertEquals(expected , actual , 0);
    
    time = new Time(2004, 11,21 , 12 , 0 , 0);
    actual = time.getGmtJulDay();
    expected = 2453331;
    assertEquals(expected , actual , 0);
    
    time = new Time(2002, 10, 1 , 12 , 0 , 0);
    actual = time.getGmtJulDay();
    expected = 2452549.0;
    assertEquals(expected , actual , 0);
    
    
    //=============================曆法分界期間 (Gregorian)==================
    time = new Time(1752, 9, 14 , 12 , 0 , 0);
    assertEquals(2361222 , time.getGmtJulDay() , 0);
    
    time = new Time(1752, 9, 13 , 12 , 0 , 0);
    assertEquals(2361221 , time.getGmtJulDay() , 0);
    
    time = new Time(1582, 10, 15 , 0 , 0 , 0);
    assertEquals(2299160.5 , time.getGmtJulDay() , 0);
    
    //=============================曆法分界期間 (Julian)==================
    time = new Time(true , 1752 , 9 , 3 , 0 , 0 , 0);
    assertEquals(2361210.5 , time.getGmtJulDay() , 0);
    
    time = new Time(true , 1752 , 9 , 2 , 0 , 0 , 0);
    assertEquals(2361209.5 , time.getGmtJulDay() , 0);
    
    time = new Time(true , 1582 , 10 , 15 , 0 , 0 , 0);
    assertEquals(2299160.5 , time.getGmtJulDay() , 0);
    
    //=========================== Julian 切換到 Gregorian ==============
    
    time = new Time(true , 1582 , 10 , 4 , 0 , 0 , 0);
    assertEquals(2299159.5 , time.getGmtJulDay() , 0);
    
    //=========================== 西元元年以及 前一年的分界 ===============
    time = new Time(true , 1 , 1 , 1 , 0 , 0 , 0);
    assertEquals(1721423.5 , time.getGmtJulDay() , 0);
    
    time = new Time(false , 1 , 12 , 31 , 0 , 0 , 0);
    assertEquals(1721422.5 , time.getGmtJulDay() , 0);
    
    time = new Time(false , 1 , 1 , 1 , 0 , 0 , 0);
    assertEquals(1721057.5 , time.getGmtJulDay() , 0);
    
    time = new Time(false , 1 , 1 , 1 , 0 , 0 , 0);
    assertEquals(1721057.5 , time.getGmtJulDay() , 0);
    
    time = new Time(false , 2 , 12 , 31 , 0 , 0 , 0);
    assertEquals(1721056.5 , time.getGmtJulDay() , 0);
    
    //===================西元 (J) 前 2637 年 2 月 2 日，甲子年？=============
    time = new Time( false , 2637 , 1 , 11 , 12 , 0 , 0);
    assertEquals(758269.0 , time.getGmtJulDay() , 0);
    
    time = new Time( false , 2637 , 2 , 2 , 12 , 0 , 0);
    assertEquals(758291.0 , time.getGmtJulDay() , 0);
    
    time = new Time(false , 2637 , 2 , 2 , 0 , 0 , 0);
    assertEquals(758290.5 , time.getGmtJulDay() , 0);
    
    
    time = new Time(false , 4713 , 1 , 1 , 12 , 0 , 0);
    assertEquals(0 , time.getGmtJulDay() , 0);
  }
}
