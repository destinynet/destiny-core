/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 05:10:36
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.chinese.StemBranch
import java.time.chrono.ChronoLocalDateTime

/** 取得日干支的介面  */
interface IDay {

  /** 是否子初換日 */
  val changeDayAfterZi: Boolean

  /** 子正實作 */
  val midnightImpl: IMidnight

  /**
   * @param gmtJulDay GMT時間
   * @param location 當地地點
   * @return 日辰干支
   */
  fun getDay(gmtJulDay: GmtJulDay, location: ILocation): StemBranch


  /**
   * @param lmt 當地時間
   * @param location 當地地點
   * @return 日辰干支
   */
  fun getDay(lmt: ChronoLocalDateTime<*>, location: ILocation): StemBranch

  /**
   * 取得 GMT 此時刻，在此地 的一日，從何時，到何時 (gmt)
   */
  fun getDayRange(gmtJulDay: GmtJulDay , location: ILocation) : Pair<GmtJulDay , GmtJulDay>
}
