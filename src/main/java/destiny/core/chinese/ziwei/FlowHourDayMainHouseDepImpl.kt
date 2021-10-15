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
 * 流時依據流日而來，以流日本宮為子時，順時鐘方向前進。
 */
@Impl([Domain(KEY_FLOW_HOUR, FlowHourDayMainHouseDepImpl.VALUE, default = true)])
class FlowHourDayMainHouseDepImpl : IFlowHour, Serializable {

  override val flowHour: FlowHour = FlowHour.MainHouseDep

  override fun getFlowHour(hour: Branch, flowDayMainHour: Branch): Branch {
    return flowDayMainHour.next(hour.getAheadOf(Branch.子))
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FlowHourDayMainHouseDepImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    const val VALUE = "dayDep"
  }
}
