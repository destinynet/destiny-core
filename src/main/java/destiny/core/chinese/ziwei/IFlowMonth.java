/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

/** 計算流月 */
public interface IFlowMonth {

  /**
   * @param flowYear 流年
   * @param flowMonth 欲求算的流月
   * @param birthMonth
   *@param birthHour 「出生」的時辰  @return
   */
  Branch getFlowMonth(Branch flowYear, Branch flowMonth, int birthMonth, Branch birthHour);
}
