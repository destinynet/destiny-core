/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch

interface IHouse<T> {

  val star: ZStar

  fun getBranch(t: T): Branch

  /**
   * @param lunarYear                  「陰曆」的年干支
   * @param solarYear                  「節氣」的年干支
   * @param monthBranch                「節氣」的月令
   * @param finalMonthNumForMonthStars 最終依據的月令數字
   * @param leap                       是否是閏月
   * @param prevMonthDays              [陰曆] 上個月有幾日
   * @param predefinedMainHouse        預先計算好的「命宮」
   */
  fun getBranch(lunarYear: StemBranch,
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
                context: ZContext): Branch


}
