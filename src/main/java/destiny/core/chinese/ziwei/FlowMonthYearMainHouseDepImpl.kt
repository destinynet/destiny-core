/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_MONTH

import java.io.Serializable

/**
 * 以流年命宮起為流月正月
 * 此種說法最有名的就是紫雲老師 還有少部分占驗派也採用
 *
 * 文件 http://imgur.com/5dLNytD
 */
@Impl([Domain(KEY_FLOW_MONTH, FlowMonthYearMainHouseDepImpl.VALUE)])
class FlowMonthYearMainHouseDepImpl : IFlowMonth, Serializable {

  override val flowMonth: FlowMonth = FlowMonth.YearMainHouseDep

  /**
   * 以流年的地支當命宮，起流月為正月
   */
  override fun getFlowMonth(flowYear: Branch, flowMonth: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return flowYear.next(flowMonth.getAheadOf(Branch.寅))
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
    const val VALUE = "yearDep"
  }

}
