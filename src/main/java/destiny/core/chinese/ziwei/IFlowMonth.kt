/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import java.util.*

/**
 * 計算流月命宮
 * 三種實作：
 *
 * 「 流年斗君，順數月」 [FlowMonthDefaultImpl]
 * 「 流月地支」        [FlowMonthFixedImpl]
 * 「 流年命宮，順數月」 [FlowMonthYearMainHouseDepImpl]
 *
 * 參考文件 : http://imgur.com/5dLNytD
 */
interface IFlowMonth : Descriptive {

  /**
   * @param flowYear   流年
   * @param flowMonth  欲求算的流月
   * @param birthMonth 「出生」的生月
   * @param birthHour  「出生」的時辰
   * @return 流月「命宮」所在地支
   */
  fun getFlowMonth(flowYear: Branch, flowMonth: Branch, birthMonth: Int, birthHour: Branch): Branch

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IFlowMonth::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}
