/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch

/**
 * FinalMonthNum
 * 月數 -> 地支
 * 「月數」 : 可能來自節氣、可能來自閏月的計算，總之，會得到一個數字
 */
abstract class HouseMonthImpl internal constructor(star: ZStar) : HouseAbstractImpl<Int>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
    return getBranch(finalMonthNumForMonthStars)
  }
}
