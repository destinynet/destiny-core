/** 2009/10/31 上午5:19:43 by smallufo */
package destiny.utils.location;

import java.io.Serializable;
import java.util.TimeZone;

/** 一些 TimeZone 的工具箱 */
public class TimeZoneUtils implements Serializable
{
  /**
   * 從時差（分鐘）找出 TimeZone , 要找最短的
   * @param minuteOffset
   */
  public static TimeZone getTimeZone(int minuteOffset)
  {
    String shortest = "GMT                            ";
    for(String tz : TimeZone.getAvailableIDs(minuteOffset*60*1000))
    {
      if (tz.length() < shortest.length())
        shortest = tz;
    }
    return TimeZone.getTimeZone(shortest.trim());
  }
}

