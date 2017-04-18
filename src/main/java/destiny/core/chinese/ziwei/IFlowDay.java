/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

/**
 * 計算流日命宮
 * 兩種實作：
 *
 * 「流日地支」         {@link FlowDayFixedImpl}
 *
 * 「流月命宮，順數日」   {@link FlowDayFlowMonthMainHouseDepImpl}
 *
 */
public interface IFlowDay {

  /**
   * @param flowMonthMainHouse 流月命宮
   */
  Branch getFlowDay(Branch flowDayBranch , int flowDayNum , Branch flowMonthMainHouse);
}
