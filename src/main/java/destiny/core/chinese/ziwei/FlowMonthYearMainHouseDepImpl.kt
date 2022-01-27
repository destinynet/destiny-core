/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 以流年命宮起為流月正月
 * 此種說法最有名的就是紫雲老師 還有少部分占驗派也採用
 *
 * 文件 http://imgur.com/5dLNytD
 */
class FlowMonthYearMainHouseDepImpl : IFlowMonth,
                                      Descriptive by FlowMonth.YearMainHouseDep.asDescriptive(),
                                      Serializable {

  /**
   * 以流年的地支當命宮，起流月為正月
   */
  override fun getFlowMonth(flowYear: Branch, flowMonth: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return flowYear.next(flowMonth.getAheadOf(Branch.寅))
  }

}
