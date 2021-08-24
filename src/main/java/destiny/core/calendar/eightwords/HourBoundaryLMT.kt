package destiny.core.calendar.eightwords

import destiny.core.astrology.TransConfig
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

class HourBoundaryLMT(private val julDayResolver: JulDayResolver) : IHourBoundary {
  override val hourBranchImpl: HourBranchConfig.HourImpl = HourBranchConfig.HourImpl.LMT

  override fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay {
    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
    val lmt = TimeTools.getLmtFromGmt(gmt, loc)
    val lmtResult = getLmtNextStartOf(lmt, loc, eb, julDayResolver, transConfig)
    val gmtResult = TimeTools.getGmtFromLmt(lmtResult, loc)
    return TimeTools.getGmtJulDay(gmtResult)
  }

  override fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver, transConfig: TransConfig): ChronoLocalDateTime<*> {
    val lmtAtHourStart = lmt.with(ChronoField.MINUTE_OF_HOUR, 0).with(ChronoField.SECOND_OF_MINUTE, 0).with(ChronoField.NANO_OF_SECOND, 0)

    return when (eb) {
      //欲求下一個子時時刻
      Branch.子 -> if (lmt.get(ChronoField.HOUR_OF_DAY) >= 23)
        lmtAtHourStart.plus(1, ChronoUnit.DAYS).with(ChronoField.HOUR_OF_DAY, 23)
      else
        lmtAtHourStart.with(ChronoField.HOUR_OF_DAY, 23)

      else     -> {
        val hourStart = eb.index * 2 - 1
        if (lmt.get(ChronoField.HOUR_OF_DAY) < hourStart)
          lmtAtHourStart.with(ChronoField.HOUR_OF_DAY, hourStart.toLong())
        else
          lmtAtHourStart.plus(1, ChronoUnit.DAYS).with(ChronoField.HOUR_OF_DAY, hourStart.toLong())
      }
    }
  }

  override fun getGmtPrevStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay {
    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
    val lmt = TimeTools.getLmtFromGmt(gmt, loc)
    val lmtResult = getLmtPrevStartOf(lmt, loc, eb, julDayResolver, transConfig)
    val gmtResult = TimeTools.getGmtFromLmt(lmtResult, loc)
    return TimeTools.getGmtJulDay(gmtResult)
  }

  override fun getLmtPrevStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver, transConfig: TransConfig): ChronoLocalDateTime<*> {
    val lmtAtHourStart = lmt.with(ChronoField.MINUTE_OF_HOUR, 0).with(ChronoField.SECOND_OF_MINUTE, 0).with(ChronoField.NANO_OF_SECOND, 0)

    val hourOfDay = lmt.get(ChronoField.HOUR_OF_DAY)
    val yesterdayHourStart = lmtAtHourStart.minus(1, ChronoUnit.DAYS)

    return when (eb) {
      Branch.子 -> if (hourOfDay < 23)
        yesterdayHourStart.with(ChronoField.HOUR_OF_DAY, 23)
      else
        lmtAtHourStart.with(ChronoField.HOUR_OF_DAY, 23)

      else     -> {
        val hourStart = eb.index * 2 - 1
        if (hourOfDay < hourStart)
          yesterdayHourStart.with(ChronoField.HOUR_OF_DAY, hourStart.toLong())
        else
          lmtAtHourStart.with(ChronoField.HOUR_OF_DAY, hourStart.toLong())
      }
    }
  }

  override fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, next: Boolean, hourBranchConfig: HourBranchConfig): ChronoLocalDateTime<*> {

    val currentHour: Branch = Lmt.getHourBranch(lmt)
    return if (next) {
      getLmtNextStartOf(lmt, loc, currentHour.next, julDayResolver, hourBranchConfig.transConfig).plus(1, ChronoUnit.HOURS)
    } else {
      getLmtPrevStartOf(lmt, loc, currentHour.prev, julDayResolver, hourBranchConfig.transConfig).plus(1, ChronoUnit.HOURS)
    }
  }
}
