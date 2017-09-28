/**
 * Created by smallufo on 2017-07-08.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.chrono.ChronoLocalDateTime;

public abstract class PositionFixedStarImpl extends AbstractPositionImpl<FixedStar> {

  PositionFixedStarImpl(FixedStar fixedStar) {
    super(fixedStar);
  }

  @Override
  public Position getPosition(ChronoLocalDateTime lmt, Location loc, Centric centric, Coordinate coordinate, StarPositionIF starPositionImpl) {
    return starPositionImpl.getPosition(getPoint() , lmt, loc , centric , coordinate);
  }
}
