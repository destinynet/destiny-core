/**
 * @author smallufo 
 * Created on 2007/12/11 at 下午 11:39:56
 */ 
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/** 
 * 區分日夜的介面 , 內定實作是 DayNightDifferentiatorImpl 
 */
public interface DayNightDifferentiator
{
  public DayNight getDayNight(Time lmt , Location location);
}
