/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.Coordinate;
import destiny.astrology.HouseSystem;
import destiny.astrology.ZodiacSign;
import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 計算上升星座（八字命宮）
 */
public interface RisingSignIF extends Descriptive {

  ZodiacSign getRisingSign(double gmtJulDay, Location location , HouseSystem houseSystem , Coordinate coordinate);

  /**
   * @param houseSystem 分宮法，大部分不會影響上升星座。
   * 但是 {@link HouseSystem#VEHLOW_EQUAL} 的確會影響上升星座！
   */
  default ZodiacSign getRisingSign(ChronoLocalDateTime lmt, Location location , HouseSystem houseSystem , Coordinate coordinate) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , location);
    return getRisingSign(gmtJulDay , location , houseSystem , coordinate);
  }

}
