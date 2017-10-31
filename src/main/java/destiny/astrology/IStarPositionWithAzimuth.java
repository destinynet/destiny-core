/**
 * @author smallufo
 * Created on 2007/5/22 at 上午 6:51:54
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 計算星體位置 + 地平方位角 (限定 Star) , <BR>
 * Swiss Ephemeris 實作為 StarPositionWithAzimuthImpl
 */
public interface IStarPositionWithAzimuth extends IStarPosition {

  PositionWithAzimuth getPosition(Star star, double gmtJulDay, Location location, Centric centric, Coordinate coordinate, double temperature, double pressure);

  default PositionWithAzimuth getPositionFromGmt(Star star, ChronoLocalDateTime gmt, Location location, Centric centric, Coordinate coordinate, double temperature, double pressure) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getPosition(star , gmtJulDay , location , centric, coordinate, temperature , pressure);
  }

  /**
   * 內定 溫度 攝氏零度，壓力 1013.25
   */
  default PositionWithAzimuth getPositionFromGmt(Star star, ChronoLocalDateTime gmt, Location location, Centric centric, Coordinate coordinate) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getPosition(star , gmtJulDay , location , centric, coordinate, 0 , 1013.25);
  }


}
