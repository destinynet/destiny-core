/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_DAY

import java.io.Serializable

/**
 * 計算流日： 固定傳回流日地支
 *
 * 說明頁面 : https://519843.blogspot.tw/2017/03/blog-post_29.html
 */
@Impl([Domain(KEY_FLOW_DAY, FlowDayBranchImpl.VALUE)])
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

  companion object {
    const val VALUE = "fixed"
  }
}
