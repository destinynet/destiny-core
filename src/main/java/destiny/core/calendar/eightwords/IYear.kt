/*
 * @author smallufo
 * @date 2004/11/29
 * @time 下午 08:14:17
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch

import java.time.chrono.ChronoLocalDateTime

/** 取得年干支的介面  */
interface IYear {

  fun getYear(gmtJulDay: Double, loc: Location): StemBranch

  /**
   *
   * @param lmt 傳入當地手錶時間
   * @param loc 傳入當地經緯度等資料
   * @return 年干支（天干地支皆傳回）
   */
  fun getYear(lmt: ChronoLocalDateTime<*>, loc: Location): StemBranch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getYear(gmtJulDay, loc)
  }

}
