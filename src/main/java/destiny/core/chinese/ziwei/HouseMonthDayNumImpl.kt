/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import java.util.*

/**
 * (月數,日數) -> 地支
 */
abstract class HouseMonthDayNumImpl internal constructor(star: ZStar) : HouseAbstractImpl<Pair<Int, Int>>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, set: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Optional<Branch>, context: ZContext): Branch {
    return getBranch(Pair(finalMonthNumForMonthStars, days))
  }
}
