/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import java.util.*

/** 求出紫微星的地支  */
interface IPurpleStarBranch : Descriptive {

  /**
   * @param state         局數
   * @param day           本月幾日
   * @param leap          本月是否是閏月
   * @param prevMonthDays 上個月有幾天
   */
  fun getBranchOfPurpleStar(state: Int, day: Int, leap: Boolean, prevMonthDays: Int): Branch

  /** 傳入的不是閏月，因此傳遞回去的 prevMonthDays 也不重要，傳回 0 即可  */
  fun getBranchOfPurpleStarNonLeap(state: Int, day: Int): Branch {
    return getBranchOfPurpleStar(state, day, false, 0)
  }

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IPurpleStarBranch::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}
