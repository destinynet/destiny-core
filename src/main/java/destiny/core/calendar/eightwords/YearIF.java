/*
 * @author smallufo
 * @date 2004/11/29
 * @time 下午 08:14:17
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import destiny.core.chinese.StemBranch;

import java.time.chrono.ChronoLocalDateTime;

/** 取得年干支的介面 */
interface YearIF {

  StemBranch getYear(double gmtJulDay , Location loc);

  /**
   *
   * @param lmt 傳入當地手錶時間
   * @param loc 傳入當地經緯度等資料
   * @return 年干支（天干地支皆傳回）
   */
  default StemBranch getYear(ChronoLocalDateTime lmt, Location loc) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , loc);
    return getYear(gmtJulDay , loc);
  }

}
