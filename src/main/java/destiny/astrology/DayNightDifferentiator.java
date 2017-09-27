/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:39:56
 */
package destiny.astrology;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 區分日夜的介面 , 內定實作是 DayNightDifferentiatorImpl 
 */
public interface DayNightDifferentiator extends Descriptive {

  DayNight getDayNight(double gmtJulDay, Location location);

  default DayNight getDayNight(ChronoLocalDateTime lmt, Location location) {
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(lmt , location);
    return getDayNight(TimeTools.getGmtJulDay(gmt), location);
  }
}
