/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.StemBranch

/**
 * 計算「上個月」的陰曆有幾日
 */
interface IPrevMonthDays {

  /** 求出上個月陰曆最後一天  */
  fun getPrevMonthLastDay(cycle: Int, year: StemBranch, month: Int, leap: Boolean): ChineseDate

  /** 計算「上個月」的陰曆有幾日  */
  fun getPrevMonthDays(cycle: Int, year: StemBranch, month: Int, leap: Boolean): Int {
    return getPrevMonthLastDay(cycle, year, month, leap).day
  }
}
