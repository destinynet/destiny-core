/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Ziwei.KEY_FLOW_MONTH

import java.io.Serializable

/**
 * 紫微斗數全集 , 流年斗君 順數月
 *
 * 大多數北派 占驗派 還有部分南派 都用此法
 *
 * 古籍 : http://imgur.com/LzdunCh
 *
 * 參考資料 http://www.freehoro.net/ZWDS/Tutorial/PaiPan/19-0_Zi_Liu_NianDouJun.php
 */
@Impl([Domain(KEY_FLOW_MONTH, FlowMonthDefaultImpl.VALUE, default = true)])
class FlowMonthDefaultImpl : IFlowMonth, Serializable {

  override val flowMonth: FlowMonth = FlowMonth.Default

  override fun getFlowMonth(flowYear: Branch, flowMonth: Branch, birthMonth: Int, birthHour: Branch): Branch {
    return Ziwei
      .getFlowYearAnchor(flowYear, birthMonth, birthHour) // 先計算流年斗君
      .next(flowMonth.getAheadOf(Branch.寅)) // 順數至流月
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
    const val VALUE = "default"
  }
}
