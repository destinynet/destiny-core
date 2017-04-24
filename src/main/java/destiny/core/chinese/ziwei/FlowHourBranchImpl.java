/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;


/**
 * 計算流時命宮
 *
 * 設定於 {@link ZSettings.FlowHour#FLOW_HOUR_FIXED}
 * 固定以該時辰當作命宮
 */
public class FlowHourBranchImpl implements IFlowHour , Serializable {

  @Override
  public Branch getFlowHour(Branch hour, Branch flowDayMainHour) {
    return hour;
  }
}
