/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.寅
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_MONTH
import java.io.Serializable

/** 固定 由「寅宮」起正月 . 少部分南派用此法  */
@Impl([Domain(KEY_FLOW_MONTH, FlowMonthFixedImpl.VALUE)])
class FlowMonthFixedImpl : IFlowMonth, Serializable {

  override val flowMonth: FlowMonth = FlowMonth.Fixed

  override fun getFlowMonth(flowYear: Branch, flowMonth: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return 寅.next(flowMonth.getAheadOf(寅))
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
    const val VALUE = "fixed"
  }
}
