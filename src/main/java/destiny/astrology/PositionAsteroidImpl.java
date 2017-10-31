/**
 * Created by smallufo on 2017-07-09.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.chrono.ChronoLocalDateTime;

public abstract class PositionAsteroidImpl extends AbstractPositionImpl<Asteroid> {

  PositionAsteroidImpl(Asteroid asteroid) {
    super(asteroid);
  }

  @Override
  public Position getPosition(ChronoLocalDateTime lmt, Location loc, Centric centric, Coordinate coordinate, IStarPosition starPositionImpl) {
    return starPositionImpl.getPosition(getPoint() , lmt, loc , centric , coordinate);
  }
}
