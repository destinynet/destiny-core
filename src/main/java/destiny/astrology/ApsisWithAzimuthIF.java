/**
 * @author smallufo
 * Created on 2007/5/28 at 上午 4:22:14
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Map;

/**
 * 計算星球南北交點 
 * Swiss Ephemeris 實作是 ApsisWithAzimuthImpl
 */
public interface ApsisWithAzimuthIF extends ApsisIF {

  Map<Apsis, PositionWithAzimuth> getPositionsWithAzimuths(Star star, double gmtJulDay , Coordinate coordinate, NodeType nodeType, Location location, double temperature, double pressure);

  PositionWithAzimuth getPositionWithAzimuth(Star star, Apsis apsis, double gmtJulDay , Coordinate coordinate, NodeType nodeType, Location location, double temperature, double pressure);

  default PositionWithAzimuth getPositionWithAzimuth(Star star, Apsis apsis, ChronoLocalDateTime gmt, Coordinate coordinate, NodeType nodeType, Location location, double temperature, double pressure) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getPositionWithAzimuth(star , apsis , gmtJulDay , coordinate , nodeType , location , temperature , pressure);
  }
}
