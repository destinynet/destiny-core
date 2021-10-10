/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:51:18
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.子
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.KEY_HOUR
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.*
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS

/**
 * 最簡單 , 以當地平均時間來區隔時辰 , 兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時
 */
@Impl([Domain(KEY_HOUR, HourLmtImpl.VALUE)])
class HourLmtImpl(val julDayResolver: JulDayResolver) : IHour, Serializable {

  override fun getHour(gmtJulDay: GmtJulDay, loc: ILocation, config: HourBranchConfig): Branch {
    return Lmt.getHourBranch(gmtJulDay, loc, julDayResolver)
  }

  override fun getHour(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: HourBranchConfig): Branch {
    return Lmt.getHourBranch(lmt)
  }

  override fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, config: HourBranchConfig): GmtJulDay {

    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
    val lmt = TimeTools.getLmtFromGmt(gmt, loc)
    val lmtResult = getLmtNextStartOf(lmt, loc, eb, julDayResolver, config)
    val gmtResult = TimeTools.getGmtFromLmt(lmtResult, loc)
    return TimeTools.getGmtJulDay(gmtResult)
  }


  /**
   * 要實作，不然會有一些 round-off 的問題
   */
  override fun getLmtNextStartOf(
    lmt: ChronoLocalDateTime<*>,
    loc: ILocation,
    eb: Branch,
    julDayResolver: JulDayResolver,
    config: HourBranchConfig
  ): ChronoLocalDateTime<*> {

    val lmtAtHourStart = lmt.with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)

    return when (eb) {
      //欲求下一個子時時刻
      子 -> if (lmt.get(HOUR_OF_DAY) >= 23)
        lmtAtHourStart.plus(1, DAYS).with(HOUR_OF_DAY, 23)
      else
        lmtAtHourStart.with(HOUR_OF_DAY, 23)

      else -> {
        val hourStart = eb.index * 2 - 1
        if (lmt.get(HOUR_OF_DAY) < hourStart)
          lmtAtHourStart.with(HOUR_OF_DAY, hourStart.toLong())
        else
          lmtAtHourStart.plus(1, DAYS).with(HOUR_OF_DAY, hourStart.toLong())
      }
    }
  }


  /**
   * 取得「前一個」此地支的開始時刻
   */
  override fun getGmtPrevStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, config: HourBranchConfig): GmtJulDay {
    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
    val lmt = TimeTools.getLmtFromGmt(gmt, loc)
    val lmtResult = getLmtPrevStartOf(lmt, loc, eb, julDayResolver, config)
    val gmtResult = TimeTools.getGmtFromLmt(lmtResult, loc)
    return TimeTools.getGmtJulDay(gmtResult)
  }

  /**
   * 取得「前一個」此地支的開始時刻
   */
  override fun getLmtPrevStartOf(
    lmt: ChronoLocalDateTime<*>,
    loc: ILocation,
    eb: Branch,
    julDayResolver: JulDayResolver,
    config: HourBranchConfig
  ): ChronoLocalDateTime<*> {
    val lmtAtHourStart = lmt.with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)

    val hourOfDay = lmt.get(HOUR_OF_DAY)
    val yesterdayHourStart = lmtAtHourStart.minus(1, DAYS)

    return when (eb) {
      子 -> if (hourOfDay < 23)
        yesterdayHourStart.with(HOUR_OF_DAY, 23)
      else
        lmtAtHourStart.with(HOUR_OF_DAY, 23)

      else -> {
        val hourStart = eb.index * 2 - 1
        if (hourOfDay < hourStart)
          yesterdayHourStart.with(HOUR_OF_DAY, hourStart.toLong())
        else
          lmtAtHourStart.with(HOUR_OF_DAY, hourStart.toLong())
      }
    }
  }

  override fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, next: Boolean, julDayResolver: JulDayResolver, config: HourBranchConfig): ChronoLocalDateTime<*> {
    val currentHour: Branch = getHour(lmt, loc, config)
    return if (next) {
      getLmtNextStartOf(lmt, loc, currentHour.next, julDayResolver, config).plus(1, ChronoUnit.HOURS)
    } else {
      getLmtPrevStartOf(lmt, loc, currentHour.prev, julDayResolver, config).plus(1, ChronoUnit.HOURS)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    const val VALUE = "lmt"
  }

}
