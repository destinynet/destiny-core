/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;

/** 取得流年(的命宮) : 直接以該流年的地支為起始宮位 */
public class FlowYearDefaultImpl implements IFlowYear , Serializable {

  @Override
  public Branch getFlowYear(Branch flowYearBranch , int birthMonth , Branch birthHour) {
    return flowYearBranch;
  }
}
