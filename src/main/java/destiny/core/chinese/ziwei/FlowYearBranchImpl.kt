/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

import java.io.Serializable

/** 取得流年(的命宮) : 直接以該流年的地支為起始宮位  */
class FlowYearBranchImpl : IFlowYear, Serializable {

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


}
