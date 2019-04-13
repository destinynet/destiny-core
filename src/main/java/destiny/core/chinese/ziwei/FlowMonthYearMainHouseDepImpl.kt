/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

import java.io.Serializable

/**
 * 以流年命宮起為流月正月
 * 此種說法最有名的就是紫雲老師 還有少部分占驗派也採用
 *
 * 文件 http://imgur.com/5dLNytD
 */
class FlowMonthYearMainHouseDepImpl : IFlowMonth, Serializable {

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


}
