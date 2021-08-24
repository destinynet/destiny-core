package destiny.core.calendar.eightwords

import destiny.core.astrology.TransConfig
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import java.time.Duration
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit

interface IHourBoundary {

  val hourBranchImpl: HourBranchConfig.HourImpl

  /**
   * 取得下一個地支的開始時刻
   */
  fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay
  fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver, transConfig: TransConfig): ChronoLocalDateTime<*> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val resultGmtJulDay = getGmtNextStartOf(gmtJulDay, loc, eb, transConfig)
    return TimeTools.getLmtFromGmt(resultGmtJulDay, loc, julDayResolver)
  }

  /**
   * 取得「前一個」此地支的開始時刻
   */
  fun getGmtPrevStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay
  fun getLmtPrevStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver, transConfig: TransConfig): ChronoLocalDateTime<*> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val resultGmtJulDay = getGmtPrevStartOf(gmtJulDay, loc, eb, transConfig)
    return TimeTools.getLmtFromGmt(resultGmtJulDay, loc, julDayResolver)
  }


  /**
   * 從目前的時刻，取得下一個（或上一個）時辰中點的 LMT 時刻為何
   * @param next true = 下一個時辰 , false = 上一個時辰
   */
  fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, next: Boolean = true, hourBranchConfig: HourBranchConfig): ChronoLocalDateTime<*>


  /**
   * accessory function , 傳回當地，一日內的時辰「開始」時刻
   */
  fun getDailyBranchStartMap(day: ChronoLocalDate, loc: ILocation, julDayResolver: JulDayResolver, config: HourBranchConfig): Map<Branch, ChronoLocalDateTime<*>> {
    val lmtStart = day.atTime(LocalTime.MIDNIGHT)

    return Branch.values().map { b ->
      val lmt = if (b == Branch.子) {
        getLmtNextStartOf(lmtStart.minus(2, ChronoUnit.HOURS), loc, b, julDayResolver, config.transConfig)
      } else {
        getLmtNextStartOf(lmtStart, loc, b, julDayResolver, config.transConfig)
      }
      b to lmt
    }.sortedBy { (_, lmt) -> lmt }.toMap()
  }

  /**
   * accessory function , 傳回當地，一日內的時辰「中間」時刻
   */
  fun getDailyBranchMiddleMap(day: ChronoLocalDate, loc: ILocation, julDayResolver: JulDayResolver, config: HourBranchConfig = HourBranchConfig()): Map<Branch, ChronoLocalDateTime<*>> {
    val startTimeMap = getDailyBranchStartMap(day, loc, julDayResolver, config)

    return startTimeMap.map { (branch, startTime) ->
      val endTime = if (branch != Branch.亥) {
        startTimeMap[branch.next]
      } else {
        val start: ChronoLocalDateTime<*> = startTimeMap[branch] ?: error("")
        getLmtNextStartOf(start, loc, Branch.子, julDayResolver, config.transConfig)
      }
      branch to startTime.plus(Duration.between(startTime, endTime).dividedBy(2))
    }.sortedBy { (_, lmt) -> lmt }.toMap()
  }

}
