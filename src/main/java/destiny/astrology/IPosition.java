/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.chrono.ChronoLocalDateTime;

public interface IPosition<T extends Point> {

  T getPoint();

  Position getPosition(ChronoLocalDateTime lmt , Location loc , Centric centric , Coordinate coordinate , StarPositionIF starPositionImpl);
}
