/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch

/**
 * 年支 -> 地支
 * 其中的「年支」，可能是陰曆、也可能是節氣
 * 鍾義明 的書籍特別提出，年系星，應該用立春分界 , 參考截圖 http://imgur.com/WVUxCc8
 */
abstract class HouseYearBranchImpl internal constructor(star: ZStar) : HouseAbstractImpl<Branch>(star) {


  override fun getBranch(lunarYear: StemBranch,
                         solarYear: StemBranch,
                         monthBranch: Branch,
                         finalMonthNumForMonthStars: Int,
                         solarTerms: SolarTerms,
                         days: Int,
                         hour: Branch,
                         state: Int,
                         gender: Gender,
                         leap: Boolean,
                         prevMonthDays: Int,
                         predefinedMainHouse: Branch?,
                         context: IZiweiContext): Branch {
    val yearBranch = if (context.yearType == YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
    return getBranch(yearBranch)
  }
}
