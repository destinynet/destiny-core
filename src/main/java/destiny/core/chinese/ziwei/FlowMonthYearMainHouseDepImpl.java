/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;

/**
 * 以流年命宮起為流月正月
 * 此種說法最有名的就是紫雲老師 還有少部分占驗派也採用
 * */
public class FlowMonthYearMainHouseDepImpl implements IFlowMonth , Serializable {

  /**
   * 以流年的地支當命宮，起流月為正月
   */
  @Override
  public Branch getFlowMonth(Branch flowYear, Branch flowMonth, int birthMonth, Branch birthHour) {
    return flowYear.next(flowMonth.getAheadOf(Branch.寅));
  }
}
