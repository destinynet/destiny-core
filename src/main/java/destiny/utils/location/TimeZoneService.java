/** 2009/11/4 上午5:24:12 by smallufo */
package destiny.utils.location;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.TimeZone;


/** 從不同的 TimeZoneIF 實作，來判斷最恰當的答案 */
public class TimeZoneService implements Serializable
{
  private List<TimeZoneIF> timeZoneImpls;
  
  protected TimeZoneService()
  {
  }
  
  public TimeZoneService(List<TimeZoneIF> timeZoneImpls)
  {
    this.timeZoneImpls = timeZoneImpls;
  }
  
  public TimeZone getTimeZone(double longitude , double latitude)
  {
    for(TimeZoneIF impl : timeZoneImpls)
    {
      try
      {
        return impl.getTimeZone(longitude, latitude);
      }
      catch (IOException e)
      {
      }
    }
    return TimeZone.getTimeZone("GMT");
  }
}

