/**
 * Created by smallufo on 2017-10-15.
 */
package destiny.core;

import destiny.core.calendar.Location;
import org.jooq.lambda.tuple.Tuple2;

import java.time.chrono.ChronoLocalDateTime;
import java.util.function.Function;

/**
 * 取得一張命盤（不論是八字、斗數、或是占星）
 * 的「歲數」範圍
 */
public interface IntAge {

  /**
   * 此時刻出生的某人，在第幾歲時，範圍為何
   * @return 前者為 prior GMT 時刻，後者為 later GMT 時刻
   * */
  Tuple2<Double , Double> getRange(Gender gender , double gmtJulDay , Location loc , int age);

  default Tuple2<ChronoLocalDateTime , ChronoLocalDateTime> getRangeTime(Gender gender , double gmtJulDay , Location loc , int age , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    return getRange(gender , gmtJulDay , loc , age)
      .map1(revJulDayFunc::apply)
      .map2(revJulDayFunc::apply);
  }

}
