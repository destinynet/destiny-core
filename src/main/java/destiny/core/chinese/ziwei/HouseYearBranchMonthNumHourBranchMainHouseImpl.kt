/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch

/**
 * 利用 年支、月數、時支
 * 只有 [StarMinor.天才] 以及 [StarMinor.天壽] 在用
 */
abstract class HouseYearBranchMonthNumHourBranchMainHouseImpl internal constructor(star: ZStar) : HouseAbstractImpl<Triple<Branch, Int, Branch>>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch,
                         finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
    val yearBranch = if (context.yearType == YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
    return getBranch(Triple(yearBranch, finalMonthNumForMonthStars, hour))
  }
}
