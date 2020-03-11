/**
 * Created by smallufo on 2017-05-08.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_DAY

import java.io.Serializable

/**
 * 計算流日：「流月命宮（「不」當作該月一日），順數日」
 *
 * 參考 shan 的做法
 * https://destiny.to/ubbthreads/ubbthreads.php/topics/1424030
 */
@Impl([Domain(KEY_FLOW_DAY, FlowDaySkipFlowMonthMainHouseImpl.VALUE)])
class FlowDaySkipFlowMonthMainHouseImpl : IFlowDay, Serializable {

  override fun getFlowDay(flowDayBranch: Branch, flowDayNum: Int, flowMonthMainHouse: Branch): Branch {
    return flowMonthMainHouse.next(flowDayNum)
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
    const val VALUE = "skipMonthMain"
  }
}
