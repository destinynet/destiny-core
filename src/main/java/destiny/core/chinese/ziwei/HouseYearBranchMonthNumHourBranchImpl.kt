/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/** (年支、月數、時支) -> 地支
 *
 * 只有  [StarMinor.天壽]
 */
abstract class HouseYearBranchMonthNumHourBranchImpl internal constructor(star: ZStar) :
  HouseAbstractImpl<Triple<Branch, Int, Branch>>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    val yearBranch = context.year.branch
    return getBranch(Triple(yearBranch, context.finalMonthNumForMonthStars, context.hour))
  }

}
