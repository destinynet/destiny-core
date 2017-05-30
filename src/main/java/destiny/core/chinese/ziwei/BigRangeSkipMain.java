/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.YinYangIF;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;

import static destiny.core.chinese.ziwei.House.命宮;

/**
 * 命宮無大限
 */
public class BigRangeSkipMain implements IBigRange , Serializable {

  @Override
  public Tuple2<Integer, Integer> getVageRange(House house, int set, YinYangIF yinYang, Gender gender, IHouseSeq houseSeqImpl) {
    return getAgeRange(house , set , yinYang , gender , houseSeqImpl);
  }

  @Override
  public Tuple2<Double, Double> getRange(House house, int set, YinYangIF yinYang, Gender gender, FortuneOutput fortuneOutput, IHouseSeq houseSeqImpl) {
    switch (fortuneOutput) {
      case 虛歲: {
        return getAgeRange(house , set , yinYang , gender , houseSeqImpl)
          .map1(Integer::doubleValue)
          .map2(Integer::doubleValue);
      } // 虛歲
      default: throw new AssertionError("Not Yet Implemented : " + fortuneOutput);
    }
  }

  /** 虛歲 */
  private Tuple2<Integer , Integer> getAgeRange(House house, int set, YinYangIF yinYang, Gender gender, IHouseSeq houseSeq) {
    int steps;
    if ((yinYang.getBooleanValue() && gender == Gender.男) || (!yinYang.getBooleanValue() && gender == Gender.女)) {
      // 陽男陰女順行
      steps = houseSeq.getAheadOf(命宮.prev(1 , houseSeq) , house);
    } else {
      // 陰男陽女逆行
      steps = houseSeq.getAheadOf(house , 命宮.next(1 , houseSeq));
    }
    int fromRange = set + steps * 10;
    int toRange = set + steps * 10 + 9;
    return Tuple.tuple(fromRange , toRange);
  }
}
