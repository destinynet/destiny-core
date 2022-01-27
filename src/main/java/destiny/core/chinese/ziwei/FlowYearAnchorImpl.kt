/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import java.io.Serializable

/** 取得流年(的命宮) : 以「流年斗君」為依據  */
class FlowYearAnchorImpl : IFlowYear,
                           Descriptive by FlowYear.Anchor.asDescriptive(),
                           Serializable {

  override fun getFlowYear(flowYearBranch: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return Ziwei.getFlowYearAnchor(flowYearBranch, birthMonth, birthHour)
  }
}
