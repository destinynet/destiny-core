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
   *
   *
   * https://goo.gl/jlXXnN : 
   *
   * 依命造的出生年支作判斷，以地支三合區分為四組，也就是申子辰、巳酉丑、寅午戌、亥卯未四組。
   * 例如申年出生的人，就屬於申子辰這一組；午年出生，則屬於寅午戌。
   * 每一組最後一個地支就是四墓庫，即辰、戌、丑、未四字。
   *
   * 小限排盤簡單的方法，就是以自己出生年那組的墓支對宮起一歲，男順行、女逆行，逐年逐宮去排列。
   * 舉例：如果命造為寅年之男命，其屬於寅午戌這組。
   * 要起該命造之小限盤，就從寅午戌的戌宮之對宮，
   * 也就是辰宮起一歲、二歲在巳宮（男命順行）、三歲在午宮，如此順排下去。
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
