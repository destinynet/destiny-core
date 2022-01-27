/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 計算流日： 固定傳回流日地支
 *
 * 說明頁面 : https://519843.blogspot.tw/2017/03/blog-post_29.html
 */
class FlowDayBranchImpl : IFlowDay,
                          Descriptive by FlowDay.Branch.asDescriptive(),
                          Serializable {

  override fun getFlowDay(flowDayBranch: Branch, flowDayNum: Int, flowMonthMainHouse: Branch): Branch {
    return flowDayBranch
  }

}
