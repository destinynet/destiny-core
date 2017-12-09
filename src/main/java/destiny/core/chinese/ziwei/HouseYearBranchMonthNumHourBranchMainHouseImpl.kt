/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple3
import java.util.*

/**
 * 只有 [StarMinor.天才] 在用 : [StarMinor.fun天才]
 */
abstract class HouseYearBranchMonthNumHourBranchMainHouseImpl internal constructor(star: ZStar) : HouseAbstractImpl<Tuple3<Branch, Int, Branch>>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, set: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Optional<Branch>, context: ZContext): Branch {
    val yearBranch = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
    return getBranch(Tuple.tuple(yearBranch, finalMonthNumForMonthStars, hour))
  }
}
