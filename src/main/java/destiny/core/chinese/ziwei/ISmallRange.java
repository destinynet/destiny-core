/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;

import java.util.ArrayList;
import java.util.List;

import static destiny.core.Gender.男;
import static destiny.core.chinese.Branch.*;

/** 起小限 */
public interface ISmallRange {

  /** 取得此地支宮位的小限列表
   *
   * 小限一律男命順行，女命逆行，而不分陰陽。
   * 凡屬
   * 寅午戌年生人，由辰宮起一歲小限，
   * 申子辰年生人，由戌宮起一歲小限，
   * 亥卯未年生人，由丑宮起一歲小限，
   * 巳酉丑年生人，由未宮起一歲小限。
   *
   * 這禮的歲是指虛歲。小限每十二年轉完命盤一周，又回到原來的宮位。
   * */
  static List<Long> getRanges(Branch house , Branch birthYear , Gender gender) {
    Branch start = getStartingHouse(birthYear);
    int steps = gender==男? house.getAheadOf(start) : start.getAheadOf(house);

    int startAge = 1 + steps;

    List<Long> result = new ArrayList<>();
    for(int i=0 ;  i <6 ; i++) {
      result.add((long) (startAge + i*12));
    }

    return result;
  }

  /** 哪一宮 起 一歲小限 */
  static Branch getStartingHouse(Branch birthYear) {
    switch (birthYear) {
      case 寅: case 午: case 戌: return 辰;
      case 申: case 子: case 辰: return 戌;
      case 亥: case 卯: case 未: return 丑;
      case 巳: case 酉: case 丑: return 未;
      default: throw new AssertionError("Error : " + birthYear);
    }
  }
}
