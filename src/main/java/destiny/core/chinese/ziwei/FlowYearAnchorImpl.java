/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;

/** 取得流年(的命宮) : 以「流年斗君」為依據 */
public class FlowYearAnchorImpl implements IFlowYear , Serializable {

  @Override
  public Branch getFlowYear(Branch flowYearBranch , int birthMonth , Branch birthHour) {
    return IZiwei.getFlowYearAnchor(flowYearBranch , birthMonth , birthHour);
  }
}
