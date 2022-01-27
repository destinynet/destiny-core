/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import java.io.Serializable


/**
 * 計算流時命宮
 *
 * 固定以該時辰當作命宮
 */
class FlowHourBranchImpl : IFlowHour,
                           Descriptive by FlowHour.Branch.asDescriptive(),
                           Serializable {

  override fun getFlowHour(hour: Branch, flowDayMainHour: Branch): Branch {
    return hour
  }

}
