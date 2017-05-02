/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.YinYangIF;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static destiny.core.chinese.ziwei.House.命宮;

/**
 * 「命宮起大限」
 *
 * 第一大限皆自命宮起，依照命盤局數來起。
 * 水二局，就是在命宮由二歲起大限，到十一歲截止。
 * 至於第二大限，就有順逆之分。陽男陰女順行、陰男陽女逆行。所以，
 *
 * 順行的話，就是命、父、福順時針而行。
 * 逆行就是命、兄、夫逆時針而行。
 */
public class BigRangeFromMain implements IBigRange , Serializable {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public Tuple2<Double, Double> getRange(House house, int set, YinYangIF yinYang, Gender gender, FortuneOutput fortuneOutput, IHouseSeq houseSeq) {
    switch (fortuneOutput) {
      case 虛歲: {
        return getAgeRange(house , set , yinYang , gender , houseSeq)
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
      steps = houseSeq.getAheadOf(命宮 , house);
    } else {
      // 陰男陽女逆行
      steps = houseSeq.getAheadOf(house , 命宮);
    }
    int fromRange = set + steps * 10;
    int toRange = set + steps * 10 + 9;
    return Tuple.tuple(fromRange , toRange);
  }
}
