/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

import java.io.Serializable

/** 取得流年(的命宮) : 以「流年斗君」為依據  */
class FlowYearAnchorImpl : IFlowYear, Serializable {

  override fun getFlowYear(flowYearBranch: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return Ziwei.getFlowYearAnchor(flowYearBranch, birthMonth, birthHour)
  }
}
