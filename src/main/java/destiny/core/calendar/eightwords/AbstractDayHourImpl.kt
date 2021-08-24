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
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit

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

    // 下個子初時刻
    val nextZiStart: ChronoLocalDateTime<*> = hourImpl.getLmtNextStartOf(lmt, location, Branch.子, julDayResolver)

    // 下個子正時刻
    val nextMidnightLmt = midnightImpl.getNextMidnight(lmt, location, julDayResolver).let {
      val dur = Duration.between(nextZiStart, it).abs()
      if (dur.toMinutes() <= 1) {
        logger.warn("子初子正 幾乎重疊！ 可能是 DST 切換. 下個子初 = {} , 下個子正 = {} . 相隔秒 = {}", nextZiStart, it, dur.seconds) // DST 結束前一天，可能會出錯
        it.plus(1, ChronoUnit.HOURS)
      } else {
        it
      }
    }

    return getDay(lmt, location, hourImpl, nextZiStart, nextMidnightLmt, changeDayAfterZi, julDayResolver)
  } // LMT 版本

  /**
   * 取得 GMT 此時刻，在此地 的一日，從何時，到何時 (gmt)
   */
  override fun getDayRange(gmtJulDay: GmtJulDay, location: ILocation): Pair<GmtJulDay, GmtJulDay> {
    return if (changeDayAfterZi) {
      // 子初換日
      // 上個子初
      val prevZiStart = hourImpl.getGmtPrevStartOf(gmtJulDay , location , Branch.子, HourBranchConfig())
      // 下個子初
      val nextZiStart = hourImpl.getGmtNextStartOf(gmtJulDay , location , Branch.子, HourBranchConfig())
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
