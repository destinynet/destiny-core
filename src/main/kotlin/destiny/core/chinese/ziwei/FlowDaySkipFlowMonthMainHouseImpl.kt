/**
 * Created by smallufo on 2017-05-08.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 計算流日：「流月命宮（「不」當作該月一日），順數日」
 *
 * 參考 shan 的做法
 * https://destiny.to/ubbthreads/ubbthreads.php/topics/1424030
 */
class FlowDaySkipFlowMonthMainHouseImpl : IFlowDay,
                                          Descriptive by FlowDay.SkipFlowMonthMainHouse.asDescriptive(),
                                          Serializable {


  override fun getFlowDay(flowDayBranch: Branch, flowDayNum: Int, flowMonthMainHouse: Branch): Branch {
    return flowMonthMainHouse.next(flowDayNum)
  }

}
