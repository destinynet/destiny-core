/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;

/**
 * 計算流日：「流月命宮，順數日」 */
public class FlowDayFlowMonthMainHouseDepImpl implements IFlowDay , Serializable {

  @Override
  public Branch getFlowDay(Branch flowDayBranch, int flowDayNum, Branch flowMonthMainHouse) {
    return flowMonthMainHouse.next(flowDayNum-1);
  }
}
