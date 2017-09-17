/**
 * Created by smallufo on 2017-07-08.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.LocalDateTime;

public abstract class PositionFixedStarImpl extends AbstractPositionImpl<FixedStar> {

  PositionFixedStarImpl(FixedStar fixedStar) {
    super(fixedStar);
  }

  @Override
  public Position getPosition(LocalDateTime lmt, Location loc, Centric centric, Coordinate coordinate, StarPositionIF starPositionImpl) {
    return starPositionImpl.getPosition(getPoint() , lmt, loc , centric , coordinate);
  }
}
