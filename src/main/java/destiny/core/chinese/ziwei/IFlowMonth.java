/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

/**
 * 計算流月命宮
 * 三種實作：
 *
 * 「 流年斗君，順數月」 {@link FlowMonthDefaultImpl}
 * 「 流月地支」        {@link FlowMonthFixedImpl}
 * 「 流年命宮，順數月」 {@link FlowMonthYearMainHouseDepImpl}
 * */
public interface IFlowMonth {

  /**
   * @param flowYear 流年
   * @param flowMonth 欲求算的流月
   * @param birthMonth 「出生」的生月
   * @param birthHour 「出生」的時辰
   */
  Branch getFlowMonth(Branch flowYear, Branch flowMonth, int birthMonth, Branch birthHour);
}
