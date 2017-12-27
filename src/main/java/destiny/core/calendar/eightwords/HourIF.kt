/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:49:08
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch

import java.time.chrono.ChronoLocalDateTime

/** 時辰的分界點實作 , SwissEph 的實作是 HourSolarTransImpl  */
interface HourIF : Descriptive {

  fun getHour(gmtJulDay: Double, location: Location): Branch


  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @return 時辰（只有地支）
   */
  fun getHour(lmt: ChronoLocalDateTime<*>, location: Location): Branch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    return getHour(gmtJulDay, location)
  }

  /**
   * @param gmtJulDay GMT 時間
   * @param location 地點
   * @param eb 下一個地支
   * @return 下一個地支的開始時刻
   */
  fun getGmtNextStartOf(gmtJulDay: Double, location: Location, eb: Branch): Double


  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @param eb 欲求之下一個地支開始時刻
   * @return 回傳 LMT 時刻
   */
  fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, location: Location, eb: Branch, revJulDayFunc: Function1<Double , ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    val resultGmtJulDay = getGmtNextStartOf(gmtJulDay, location, eb)

    val resultGmt = revJulDayFunc.invoke(resultGmtJulDay)
    return TimeTools.getLmtFromGmt(resultGmt, location)
  }

}
