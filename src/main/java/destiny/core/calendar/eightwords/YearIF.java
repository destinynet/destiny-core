/*
 * @author smallufo
 * @date 2004/11/29
 * @time 下午 08:14:17
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.StemBranch;

import java.time.LocalDateTime;

/** 取得年干支的介面 */
interface YearIF {

  /**
   *
   * @param lmt 傳入當地手錶時間
   * @param location 傳入當地經緯度等資料
   * @return 年干支（天干地支皆傳回）
   */
  StemBranch getYear(LocalDateTime lmt, Location location);

  /**
   * @param lmt      傳入當地手錶時間
   * @param location 傳入當地經緯度等資料
   * @return 年干支（天干地支皆傳回）
   */
  default StemBranch getYear(Time lmt, Location location) {
    return getYear(lmt.toLocalDateTime() , location);
  }
}
