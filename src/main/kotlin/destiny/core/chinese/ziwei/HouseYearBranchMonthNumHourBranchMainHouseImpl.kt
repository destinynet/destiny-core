/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * 利用 年支、月數、時支
 * 只有 [StarMinor.天才] 以及 [StarMinor.天壽] 在用
 */
abstract class HouseYearBranchMonthNumHourBranchMainHouseImpl internal constructor(star: ZStar) :
  HouseAbstractImpl<Triple<Branch, Int, Branch>>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    val yearBranch = context.year.branch
    return getBranch(Triple(yearBranch, context.finalMonthNumForMonthStars, context.hour))
  }

}
