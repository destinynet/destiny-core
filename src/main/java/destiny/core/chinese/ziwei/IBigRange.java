/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.YinYangIF;
import org.jooq.lambda.tuple.Tuple2;

/** 起大限 */
public interface IBigRange {

  Tuple2<Double , Double> getRange(House house, int set, YinYangIF yinYang, Gender gender, IZiwei.RangeType rangeType, IHouseSeq houseSeq);
}
