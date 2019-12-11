/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import java.util.*

/** 流時命宮  */
interface IFlowHour : Descriptive {

  /**
   * @param hour  欲求算的當日時辰
   * @param flowDayMainHour 當日命宮
   */
  fun getFlowHour(hour: Branch, flowDayMainHour: Branch): Branch

  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IFlowHour::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }



}
