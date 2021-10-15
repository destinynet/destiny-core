/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_YEAR

import java.io.Serializable

/** 取得流年(的命宮) : 直接以該流年的地支為起始宮位  */
@Impl([Domain(KEY_FLOW_YEAR, FlowYearBranchImpl.VALUE)])
class FlowYearBranchImpl : IFlowYear, Serializable {

  override val flowYear: FlowYear = FlowYear.Branch

  override fun getFlowYear(flowYearBranch: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return flowYearBranch
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    const val VALUE = "branch"
  }

}
