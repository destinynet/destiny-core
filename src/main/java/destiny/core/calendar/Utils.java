package destiny.core.calendar;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class Utils implements Serializable
{
  /**
  * LST : Local Sidereal Time , 地方恆星時
  * http://scienceworld.wolfram.com/astronomy/LocalSiderealTime.html
  * Practical Astronomy with Your Calculator, 3rd ed , Page 20
  */
  public static Calendar getLST(@NotNull Calendar LMTCal ,
                                         int LongitudeDegree , int LongitudeMinute, double LongitudeSecond )
  {
    int GMTLMTOffset =  LMTCal.getTimeZone().getOffset( LMTCal.getTimeInMillis() );
    //台北，傳回 28800000 (8hrs)
    
    //一度 = 4 分鐘 = 240 秒 = 240000 miliseconds
    //一分 =            4 秒 =   4000 miliseconds
    //一秒 =                       66.66666 milis = 100*2/3
    int LST_LMT_Offset = (int) ( LongitudeDegree * 240000 
                               + LongitudeMinute *   4000
                               + LongitudeSecond *    100*2/3 
                               - GMTLMTOffset);
    //System.out.println("LST_LMT_Offset =  " + LST_LMT_Offset );
    Calendar LSTCal = Calendar.getInstance();
    LSTCal.setTimeInMillis(LMTCal.getTimeInMillis()+LST_LMT_Offset);
    return LSTCal;
  }
  
  /**
  * LST : Local Sidereal Time , 地方恆星時
  * http://scienceworld.wolfram.com/astronomy/LocalSiderealTime.html
  * Practical Astronomy with Your Calculator, 3rd ed , Page 20
  */
  public static Calendar getLST(@NotNull Calendar LMTCal ,
                                int LongitudeDegree , int LongitudeMinute )
  {
    return getLST(LMTCal , LongitudeDegree , LongitudeMinute , 0 );
  }
  
  /**
  * LST : Local Sidereal Time , 地方恆星時
  * http://scienceworld.wolfram.com/astronomy/LocalSiderealTime.html
  * Practical Astronomy with Your Calculator, 3rd ed , Page 20
  */
  public static Calendar getLST(int Year , int Month , int Day , int Hour , int Minute , int Second , double OffSetHour ,
                                int LongitudeDegree , int LongitudeMinute, double LongitudeSecond )
  {
    Calendar LMTCal = new GregorianCalendar(Year , Month , Day , Hour , Minute , Second);
    TimeZone tz = new SimpleTimeZone( (int)OffSetHour*60*60*1000 , "tempTimeZone");
    LMTCal.setTimeZone(tz);
    return getLST(LMTCal , LongitudeDegree , LongitudeMinute , LongitudeSecond );
  }
  
}
