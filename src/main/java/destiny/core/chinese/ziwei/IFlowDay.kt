/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import java.util.*

/**
 * 計算流日命宮
 * 兩種實作：
 *
 * 「流日地支」         [FlowDayBranchImpl]
 *
 * 「流月命宮（亦作為該月一日），順數日」   [FlowDayFromFlowMonthMainHouseImpl]
 *
 * 「流月命宮（略過不當初一），順數日」 [FlowDaySkipFlowMonthMainHouseImpl]
 *
 */
interface IFlowDay : Descriptive {

  /**
   * @param flowMonthMainHouse 流月命宮
   */
  fun getFlowDay(flowDayBranch: Branch, flowDayNum: Int, flowMonthMainHouse: Branch): Branch

  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IFlowDay::class.qualifiedName!!, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }
}
