/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:39:56
 */
package destiny.astrology;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

import java.time.LocalDateTime;

/**
 * 區分日夜的介面 , 內定實作是 DayNightDifferentiatorImpl 
 */
public interface DayNightDifferentiator extends Descriptive {

  DayNight getDayNight(double gmtJulDay, Location location);

  default DayNight getDayNight(LocalDateTime lmt, Location location) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    return getDayNight(Time.getGmtJulDay(gmt), location);
  }
}
