/**
 * Created by smallufo on 2017-10-15.
 */
package destiny.core;

import destiny.core.calendar.Location;
import org.jooq.lambda.tuple.Tuple2;

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

}
