/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.chinese.TianyiIF
import java.util.*

/**
 * (年干,天乙貴人設定) -> 地支
 * 適用於 [StarLucky.天魁] (陽貴人) , [StarLucky.天鉞] (陰貴人)
 * 2017-05-20 新增 [ZContext.YearType] 的判斷
 */
abstract class HouseYearStemTianyiImpl internal constructor(star: ZStar) : HouseAbstractImpl<Pair<Stem, TianyiIF>>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
    val yearStem = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.stem else solarYear.stem
    return getBranch(Pair(yearStem, context.tianyiImpl))
  }
}