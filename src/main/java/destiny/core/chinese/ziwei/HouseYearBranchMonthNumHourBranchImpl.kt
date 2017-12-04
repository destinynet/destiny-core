/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import java.util.*

/** (年支、月數、時支) -> 地支
 *
 * 只有  [StarMinor.天壽]
 */
abstract class HouseYearBranchMonthNumHourBranchImpl internal constructor(star: ZStar) : HouseAbstractImpl<Triple<Branch, Int, Branch>>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, set: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Optional<Branch>, context: ZContext): Branch {
    val yearBranch = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
    return getBranch(Triple(yearBranch, finalMonthNumForMonthStars, hour))
  }
}
