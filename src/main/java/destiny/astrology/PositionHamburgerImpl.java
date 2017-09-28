/**
 * Created by smallufo on 2017-07-08.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.chrono.ChronoLocalDateTime;

public abstract class PositionHamburgerImpl extends AbstractPositionImpl<Hamburger> {

  PositionHamburgerImpl(Hamburger hamburger) {
    super(hamburger);
  }

  @Override
  public Position getPosition(ChronoLocalDateTime lmt, Location loc, Centric centric, Coordinate coordinate, StarPositionIF starPositionImpl) {
    return starPositionImpl.getPosition(getPoint() , lmt, loc , centric , coordinate);
  }
}
