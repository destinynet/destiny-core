/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

import java.io.Serializable


/**
 * 計算流時命宮
 *
 * 固定以該時辰當作命宮
 */
class FlowHourBranchImpl : IFlowHour, Serializable {

  override fun getFlowHour(hour: Branch, flowDayMainHour: Branch): Branch {
    return hour
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FlowHourBranchImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


}
