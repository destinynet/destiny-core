/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.Coordinate;
import destiny.astrology.HouseSystem;
import destiny.astrology.ZodiacSign;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

import java.time.LocalDateTime;

/**
 * 計算上升星座（八字命宮）
 */
public interface RisingSignIF {

  ZodiacSign getRisingSign(double gmtJulDay, Location location , HouseSystem houseSystem , Coordinate coordinate);

  /**
   * @param hs 分宮法，大部分不會影響上升星座。
   *           但是 {@link HouseSystem#VEHLOW_EQUAL} 的確會影響上升星座！
   */
  default ZodiacSign getRisingSign(Time lmt, Location location , HouseSystem hs , Coordinate coordinate) {
    Time gmt = Time.getGMTfromLMT(lmt , location);
    return getRisingSign(gmt.getGmtJulDay() , location , hs , coordinate);
  }

  default ZodiacSign getRisingSign(LocalDateTime lmt, Location location , HouseSystem houseSystem , Coordinate coordinate) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getRisingSign(gmtJulDay , location , houseSystem , coordinate);
  }

  String getRisingSignName();

}
