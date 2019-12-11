/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import java.util.*

/** 流年排法(的命宮) : 目前只有兩個實作 : 「 流年地支」或「 流年斗君」。  */
interface IFlowYear : Descriptive {

  fun getFlowYear(flowYearBranch: Branch, birthMonth: Int, birthHour: Branch): Branch

  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IFlowYear::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }
}
