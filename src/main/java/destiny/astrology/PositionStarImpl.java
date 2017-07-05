/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.LocalDateTime;

public abstract class PositionStarImpl extends AbstractPositionImpl<Star> {

  PositionStarImpl(Star star) {
    super(star);
  }

  @Override
  public Position getPosition(LocalDateTime ldt, Location loc, Centric centric, Coordinate coordinate, StarPositionIF starPositionImpl) {
    return starPositionImpl.getPosition(getPoint() , ldt , loc , centric , coordinate);
  }
}
