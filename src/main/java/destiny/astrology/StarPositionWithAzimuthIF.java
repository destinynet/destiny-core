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

  PositionWithAzimuth getPosition(Star star, double gmtJulDay, Location location, Centric centric, Coordinate coordinate, double temperature, double pressure);

  default PositionWithAzimuth getPositionFromGmt(Star star, LocalDateTime gmt, Location location, Centric centric, Coordinate coordinate, double temperature, double pressure) {
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getPosition(star , gmtJulDay , location , centric, coordinate, temperature , pressure);
  }


}
