/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_HOUR
import java.io.Serializable


/**
 * 計算流時命宮
 *
 * 固定以該時辰當作命宮
 */
@Impl([Domain(KEY_FLOW_HOUR, FlowHourBranchImpl.VALUE)])
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

  companion object {
    const val VALUE = "fixed"
  }

}
