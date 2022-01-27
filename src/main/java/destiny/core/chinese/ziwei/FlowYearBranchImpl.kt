/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import java.io.Serializable

/** 取得流年(的命宮) : 直接以該流年的地支為起始宮位  */
class FlowYearBranchImpl : IFlowYear,
                           Descriptive by FlowYear.Branch.asDescriptive(),
                           Serializable {

  override fun getFlowYear(flowYearBranch: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return flowYearBranch
  }

}
