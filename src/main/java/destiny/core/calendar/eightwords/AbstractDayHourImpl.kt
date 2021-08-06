/**
 * Created by smallufo on 2019-05-03.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

abstract class AbstractDayHourImpl(override val hourImpl: IHour ,
                                   val julDayResolver: JulDayResolver) : IDayHour , IHour by hourImpl , Serializable {

  /**
   * Note : 2017-10-27 : gmtJulDay 版本不方便計算，很 buggy , 改以呼叫 LMT 版本來實作
   */
  override fun getDay(gmtJulDay: GmtJulDay, location: ILocation): StemBranch {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, location, julDayResolver)

    return getDay(lmt, location)
  } // GMT 版本


  override fun getDay(lmt: ChronoLocalDateTime<*>, location: ILocation): StemBranch {

    return getDayAsLmt(lmt, location, hourImpl, midnightImpl, changeDayAfterZi, julDayResolver)
  } // LMT 版本

  /**
   * 取得 GMT 此時刻，在此地 的一日，從何時，到何時 (gmt)
   */
  override fun getDayRange(gmtJulDay: GmtJulDay, location: ILocation): Pair<GmtJulDay, GmtJulDay> {
    return if (changeDayAfterZi) {
      // 子初換日
      // 上個子初
      val prevZiStart = hourImpl.getGmtPrevStartOf(gmtJulDay , location , Branch.子)
      // 下個子初
      val nextZiStart = hourImpl.getGmtNextStartOf(gmtJulDay , location , Branch.子)
      prevZiStart to nextZiStart
    } else {
      // 子正換日
      // 上個子正
      val prevZiCenter = midnightImpl.getPrevMidnight(gmtJulDay , location)
      // 下個子正
      val nextZiCenter = midnightImpl.getNextMidnight(gmtJulDay , location)
      prevZiCenter to nextZiCenter
    }
  }

}
