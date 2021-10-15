/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_YEAR

import java.io.Serializable

/** 取得流年(的命宮) : 以「流年斗君」為依據  */
@Impl([Domain(KEY_FLOW_YEAR, FlowYearAnchorImpl.VALUE, default = true)])
class FlowYearAnchorImpl : IFlowYear, Serializable {

  override val flowYear: FlowYear = FlowYear.Anchor

  override fun getFlowYear(flowYearBranch: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return Ziwei.getFlowYearAnchor(flowYearBranch, birthMonth, birthHour)
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
    const val VALUE = "anchor"
  }
}
