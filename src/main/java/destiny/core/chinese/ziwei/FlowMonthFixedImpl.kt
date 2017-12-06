/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.寅
import java.io.Serializable

/** 固定 由「寅宮」起正月 . 少部分南派用此法  */
class FlowMonthFixedImpl : IFlowMonth, Serializable {

  override fun getFlowMonth(flowYear: Branch, flowMonth: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return 寅.next(flowMonth.getAheadOf(寅))
  }
}
