/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.calendar.chinese

import destiny.core.chinese.Branch

/**
 * 農曆日期＋時辰(地支)的表示法
 */
class ChineseDateHour(chineseDate: ChineseDate, val hourBranch: Branch)
  : ChineseDate(chineseDate.cycleOrZero, chineseDate.year,
                chineseDate.month, chineseDate.isLeapMonth,
                chineseDate.day)
