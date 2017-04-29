/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.chinese.Branch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

import static destiny.core.chinese.Branch.*;

/**
 * 閏月排紫微星
 *
 * 翰學居士 張寶丹 老師 , 高段紫微斗數 三本書
 *
 * 參考此表格 :  http://imgur.com/87sHQOq
 */
public class PurpleStarBranchLeapAccumDaysImpl implements IPurpleStarBranch , Serializable {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public Branch getBranchOfPurpleStar(int set, int day, boolean leap, int prevMonthDays) {
    if (day + prevMonthDays <= 30) {
      logger.error("日數 = {} , 加上前一個月的天數 {}  , 小於 30 日，不適用此 「日數累加推算紫微」演算法" , day , prevMonthDays );
      throw new RuntimeException("Error : 局數 = " + set + " , day = " + day + " , 閏月 = " + leap + " , 前一個月日數 = " + prevMonthDays);
    }

    if (!leap) {
      return getBranchOfPurpleStar(set , day);
    } else {
      // 閏月
      // 取得新的日數
      int newDays = prevMonthDays + day;
      if (set == 2)
        return water2(newDays);
      else if (set == 3)
        return wood3(newDays);
      else if (set == 4)
        return gold4(newDays);
      else if (set == 5)
        return earth5(newDays);
      else if (set == 6)
        return fire6(newDays);
      else
        throw new AssertionError("Error set : " + set);
    } // 閏月
  }

  /** 水二局 */
  Branch water2(int newDays) {
    if (newDays == 31)
      return 辰;
    int branchIndex = ((newDays - 32) / 2 % 12 ) +5;
    return Branch.get(branchIndex);
  }

  /** 木三局 */
  Branch wood3(int newDays) {
    return woodMap.get(newDays);
  }

  /** 金四局 */
  Branch gold4(int newDays) {
    return goldMap.get(newDays);
  }

  /** 土五局 */
  Branch earth5(int newDays) {
    return earthMap.get(newDays);
  }

  /** 火六局 */
  Branch fire6(int newDays) {
    return fireMap.get(newDays);
  }

  private Map<Integer , Branch> woodMap = new ImmutableMap.Builder<Integer, Branch>()
    .put(31 , 寅)
    .put(32 , 亥)
    .put(33 , 子)
    .put(34 , 卯)
    .put(35 , 子)
    .put(36 , 丑)
    .put(37 , 辰)
    .put(38 , 丑)
    .put(39 , 寅)
    .put(40 , 巳)
    .put(41 , 寅)
    .put(42 , 卯)
    .put(43 , 午)
    .put(44 , 卯)
    .put(45 , 辰)
    .put(46 , 未)
    .put(47 , 辰)
    .put(48 , 巳)
    .put(49 , 申)
    .put(50 , 巳)
    .put(51 , 午)
    .put(52 , 酉)
    .put(53 , 午)
    .put(54 , 未)
    .put(55 , 戌)
    .put(56 , 未)
    .put(57 , 申)
    .put(58 , 亥)
    .put(59 , 申)
    .put(60 , 酉)
    .build();


  private Map<Integer , Branch> goldMap = new ImmutableMap.Builder<Integer, Branch>()
    .put(31 , 申 )
    .put(32 , 酉 )
    .put(33 , 未 )
    .put(34 , 子 )
    .put(35 , 酉 )
    .put(36 , 戌 )
    .put(37 , 申 )
    .put(38 , 丑 )
    .put(39 , 戌 )
    .put(40 , 亥 )
    .put(41 , 酉 )
    .put(42 , 寅 )
    .put(43 , 亥 )
    .put(44 , 子 )
    .put(45 , 戌 )
    .put(46 , 卯 )
    .put(47 , 子 )
    .put(48 , 丑 )
    .put(49 , 亥 )
    .put(50 , 辰 )
    .put(51 , 丑 )
    .put(52 , 寅 )
    .put(53 , 子 )
    .put(54 , 巳 )
    .put(55 , 寅 )
    .put(56 , 卯 )
    .put(57 , 丑 )
    .put(58 , 午 )
    .put(59 , 卯 )
    .put(60 , 辰 )
    .build();


  private Map<Integer , Branch> earthMap = new ImmutableMap.Builder<Integer, Branch>()
    .put(31 , 子 )
    .put(32 , 巳 )
    .put(33 , 戌 )
    .put(34 , 未 )
    .put(35 , 申 )
    .put(36 , 丑 )
    .put(37 , 午 )
    .put(38 , 亥 )
    .put(39 , 申 )
    .put(40 , 酉 )
    .put(41 , 寅 )
    .put(42 , 未 )
    .put(43 , 子 )
    .put(44 , 酉 )
    .put(45 , 戌 )
    .put(46 , 卯 )
    .put(47 , 申 )
    .put(48 , 丑 )
    .put(49 , 戌 )
    .put(50 , 亥 )
    .put(51 , 辰 )
    .put(52 , 酉 )
    .put(53 , 寅 )
    .put(54 , 亥 )
    .put(55 , 子 )
    .put(56 , 巳 )
    .put(57 , 戌 )
    .put(58 , 卯 )
    .put(59 , 子 )
    .put(60 , 丑 )
    .build();

  private Map<Integer , Branch> fireMap = new ImmutableMap.Builder<Integer, Branch>()
    .put(31 , 寅 )
    .put(32 , 亥 )
    .put(33 , 辰 )
    .put(34 , 酉 )
    .put(35 , 午 )
    .put(36 , 未 )
    .put(37 , 卯 )
    .put(38 , 子 )
    .put(39 , 巳 )
    .put(40 , 戌 )
    .put(41 , 未 )
    .put(42 , 申 )
    .put(43 , 辰 )
    .put(44 , 丑 )
    .put(45 , 午 )
    .put(46 , 亥 )
    .put(47 , 申 )
    .put(48 , 酉 )
    .put(49 , 巳 )
    .put(50 , 寅 )
    .put(51 , 未 )
    .put(52 , 子 )
    .put(53 , 酉 )
    .put(54 , 戌 )
    .put(55 , 午 )
    .put(56 , 卯 )
    .put(57 , 申 )
    .put(58 , 丑 )
    .put(59 , 戌 )
    .put(60 , 亥 )
    .build();

}
