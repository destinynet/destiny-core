/**
 * Created by smallufo on 2017-07-09.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.LocalDateTime;

public abstract class PositionLunarPointImpl extends AbstractPositionImpl<LunarPoint> {

  PositionLunarPointImpl(LunarPoint lunarPoint) {
    super(lunarPoint);
  }

  @Override
  public Position getPosition(LocalDateTime ldt, Location loc, Centric centric, Coordinate coordinate, StarPositionIF starPositionImpl) {
    return starPositionImpl.getPosition(getPoint() , ldt , loc , centric , coordinate);
  }
}
