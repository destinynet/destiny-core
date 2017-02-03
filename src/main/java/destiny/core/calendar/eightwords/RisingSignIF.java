/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.Coordinate;
import destiny.astrology.ZodiacSign;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 計算上升星座（八字命宮）
 */
public interface RisingSignIF {

  ZodiacSign getRisingSign(Time lmt, Location location , Coordinate coordinate);

  String getRisingSignName();

}
