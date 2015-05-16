/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.ZodiacSign;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 計算上升星座（八字命宮）
 */
public interface RisingSignIF {

  enum method {
    ASTRO,  // 以占星的角度計算上升星座
    TRAD    // 以傳統的演算法計算命宮（不精確）
  }

  ZodiacSign getRisingSign(Time lmt, Location location);

  String getRisingSignName();

}
