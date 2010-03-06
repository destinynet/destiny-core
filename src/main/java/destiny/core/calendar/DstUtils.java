/** 2009/11/28 下午6:03:01 by smallufo */
package destiny.core.calendar;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import destiny.utils.Tuple;

public class DstUtils implements Serializable
{
  /** 確認此時刻，是否有DST。不論是否有沒有DST，都傳回與GMT誤差幾秒 */
  public final static Tuple<Boolean, Double> getDstSecondOffset(Time lmt , Location loc)
  {
    TimeZone tz = loc.getTimeZone();
    GregorianCalendar cal = new GregorianCalendar(lmt.getYear() , lmt.getMonth()-1 , lmt.getDay() , lmt.getHour() , lmt.getMinute() , (int)lmt.getSecond());
    boolean dst = tz.inDaylightTime(cal.getTime());
    double secondOffset = tz.getOffset(cal.getTimeInMillis()) / 1000;
    return new Tuple<Boolean , Double>(Boolean.valueOf(dst) , Double.valueOf(secondOffset));
  }
}

