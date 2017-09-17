/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.time.LocalDateTime;

public interface IPosition<T extends Point> {

  T getPoint();

  Position getPosition(LocalDateTime lmt , Location loc , Centric centric , Coordinate coordinate , StarPositionIF starPositionImpl);

//  default Position getPosition(double gmtJulDay , Location loc , Centric centric , Coordinate coordinate , StarPositionIF starPositionImpl) {
//    Tuple2<ChronoLocalDate , LocalTime> t2 =  Time.from(gmtJulDay);
//    LocalDateTime gmt = LocalDateTime.of(LocalDate.from(t2.v1()) , t2.v2());
//    return getPosition(gmt , loc , centric , coordinate , starPositionImpl);
//  }

}
