/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

/** 求出紫微星的地支 */
public interface IPurpleStarBranch {

  /**
   * @param set           局數
   * @param day           本月幾日
   * @param leap          本月是否是閏月
   * @param prevMonthDays 上個月有幾天
   */
  Branch getBranchOfPurpleStar(int set, int day, boolean leap, int prevMonthDays);

  /** 傳入的不是閏月，因此傳遞回去的 prevMonthDays 也不重要，傳回 0 即可 */
  default Branch getBranchOfPurpleStar(int set , int day) {
    return getBranchOfPurpleStar(set , day , false, 0);
  }
}
