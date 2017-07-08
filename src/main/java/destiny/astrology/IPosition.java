/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.LocalDateTime;

public interface IPosition<T extends Point> {

  T getPoint();

  <U extends Position> U getPosition(LocalDateTime ldt , Location loc , Centric centric , Coordinate coordinate , StarPositionIF<U> starPositionImpl);

}
