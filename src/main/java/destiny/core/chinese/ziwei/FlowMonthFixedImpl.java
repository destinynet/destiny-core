/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;

import static destiny.core.chinese.Branch.寅;

/** 固定 由「寅宮」起正月 . 少部分南派用此法 */
public class FlowMonthFixedImpl implements IFlowMonth , Serializable {

  @Override
  public Branch getFlowMonth(Branch flowYear, Branch flowMonth, int birthMonth, Branch birthHour) {
    return 寅.next(flowMonth.getAheadOf(寅));
  }
}
