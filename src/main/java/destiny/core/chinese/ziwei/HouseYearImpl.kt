/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import java.util.*

/**
 * 年干支 ，用於旬空兩顆星
 * 必須判斷 [ZContext.YearType]
 */
abstract class HouseYearImpl internal constructor(star: ZStar) : HouseAbstractImpl<StemBranch>(star) {


  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, set: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Optional<Branch>, context: ZContext): Branch {
    val year = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear else solarYear

    return getBranch(year)
  }
}
