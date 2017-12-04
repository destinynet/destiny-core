/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import java.util.*

/**
 * 年干 系星
 * 必須判斷 [ZContext.YearType]
 */
abstract class HouseYearStemImpl internal constructor(star: ZStar) : HouseAbstractImpl<Stem>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, set: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Optional<Branch>, context: ZContext): Branch {
    val yearStem = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.stem else solarYear.stem
    return getBranch(yearStem)
  }
}
