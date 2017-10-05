/**
 * Created by smallufo on 2017-09-25.
 */
package destiny.core.calendar;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.time.Instant;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;

/**
 * 從 julian day 轉換成各個曆法表示的介面
 */
public interface JulDayResolver {

  ChronoLocalDateTime getLocalDateTime(double gmtJulDay);

  /** 從 gmt instant 轉為 GMT Time */
  ChronoLocalDateTime getLocalDateTime(Instant gmtInstant);

  default Tuple2<ChronoLocalDate , LocalTime> getDateAndTime(double gmtJulDay) {
    ChronoLocalDateTime dateTime = getLocalDateTime(gmtJulDay);
    return Tuple.tuple(dateTime.toLocalDate() , dateTime.toLocalTime());
  }

}
