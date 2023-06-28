/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 計算流日：「流月命宮（同時當作該月一日），順數日」
 */
class FlowDayFromFlowMonthMainHouseImpl : IFlowDay,
                                          Descriptive by FlowDay.FromFlowMonthMainHouse.asDescriptive(),
                                          Serializable {

  override fun getFlowDay(flowDayBranch: Branch, flowDayNum: Int, flowMonthMainHouse: Branch): Branch {
    return flowMonthMainHouse.next(flowDayNum - 1)
  }
}
