/**
 * @author smallufo
 * Created on 2007/5/22 at 上午 6:51:54
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

import java.time.LocalDateTime;

/**
 * 計算星體位置 + 地平方位角 (限定 Star) , <BR>
 * Swiss Ephemeris 實作為 StarPositionWithAzimuthImpl
 */
public interface StarPositionWithAzimuthIF extends StarPositionIF {

  PositionWithAzimuth getPositionWithAzimuth(Star star, double gmtJulDay, Location location, double temperature, double pressure,
                                             Centric centric, Coordinate coordinate);

  default PositionWithAzimuth getPositionWithAzimuth(Star star, LocalDateTime gmt, Location location, double temperature, double pressure,
                                                     Centric centric, Coordinate coordinate) {
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getPositionWithAzimuth(star , gmtJulDay , location , temperature , pressure , centric , coordinate);
  }

}
