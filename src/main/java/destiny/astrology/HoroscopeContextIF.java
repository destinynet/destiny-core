/**
 * Created by smallufo on 2017-09-19.
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public interface HoroscopeContextIF extends Serializable {

  HoroscopeIF getHoroscope();

  @NotNull
  LocalDateTime getLmt();

  @NotNull
  LocalDateTime getGmt();

  @NotNull
  Location getLocation();

  /**
   * 取得某星位於第幾宮
   *
   * @param point 某星
   * @return 1 <= point <= 12
   */
  int getHouse(Point point);

  /** 取得星體的位置以及地平方位角 */
  PositionWithAzimuth getPosition(Point point);

  /** 取得某星 位於什麼星座 */
  ZodiacSign getZodiacSign(Point point);

  HouseSystem getHouseSystem();

  Centric getCentric();

  Coordinate getCoordinate();

  List<Point> getHousePoints(int index);
}
