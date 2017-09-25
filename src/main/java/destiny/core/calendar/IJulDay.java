/**
 * Created by smallufo on 2017-09-25.
 */
package destiny.core.calendar;

import org.jooq.lambda.tuple.Tuple2;

import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;

/**
 * 從 julian day 轉換成各個曆法表示的介面
 */
public interface IJulDay {

  Tuple2<ChronoLocalDate , LocalTime> toDateAndTime(double gmtJulDay);

  default ChronoLocalDateTime toDateTime(double gmtJulDay) {
    Tuple2<ChronoLocalDate, LocalTime>  dateAndTime = toDateAndTime(gmtJulDay);
    ChronoLocalDate date = dateAndTime.v1();
    LocalTime time = dateAndTime.v2();
    return date.atTime(time);
  }

}
