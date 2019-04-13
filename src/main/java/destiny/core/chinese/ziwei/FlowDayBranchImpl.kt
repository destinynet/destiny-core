/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

import java.io.Serializable

/**
 * 計算流日： 固定傳回流日地支
 *
 * 說明頁面 : https://519843.blogspot.tw/2017/03/blog-post_29.html
 */
class FlowDayBranchImpl : IFlowDay, Serializable {

  override fun getFlowDay(flowDayBranch: Branch, flowDayNum: Int, flowMonthMainHouse: Branch): Branch {
    return flowDayBranch
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
