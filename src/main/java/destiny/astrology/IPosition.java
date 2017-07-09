/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jooq.lambda.tuple.Tuple2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;

public interface IPosition<T extends Point> {

  T getPoint();

  Position getPosition(LocalDateTime ldt , Location loc , Centric centric , Coordinate coordinate , StarPositionIF starPositionImpl);

  default Position getPosition(double gmtJulDay , Location loc , Centric centric , Coordinate coordinate , StarPositionIF starPositionImpl) {
    Tuple2<ChronoLocalDate , LocalTime> t2 =  Time.from(gmtJulDay);
    LocalDateTime ldt = LocalDateTime.of(LocalDate.from(t2.v1()) , t2.v2());
    return getPosition(ldt , loc , centric , coordinate , starPositionImpl);
  }

}
