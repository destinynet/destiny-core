/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.calendar.chinese

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch

/**
 * 農曆日期＋時辰的表示法
 */
class ChineseDateHour : ChineseDate {

  val hourBranch: Branch

  constructor(chineseDate: ChineseDate, hour: Branch) :
    super(chineseDate.cycleOrZero, chineseDate.year,
          chineseDate.month, chineseDate.isLeapMonth,
          chineseDate.day) {
    this.hourBranch = hour
  }

  constructor(cycle: Int, year: StemBranch, month: Int, leapMonth: Boolean, day: Int, hour: Branch) :
    super(cycle, year, month, leapMonth, day) {
    this.hourBranch = hour
  }
}
