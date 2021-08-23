/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:49:08
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
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

/** 時辰的分界點實作 , SwissEph 的實作是 [HourSolarTransImpl]  */
interface IHour : Descriptive {

  fun getHour(gmtJulDay: GmtJulDay, loc: ILocation): Branch


  /**
   * @param lmt 傳入當地手錶時間
   * @param loc 當地的經緯度等資料
   * @return 時辰（只有地支）
   */
  fun getHour(lmt: ChronoLocalDateTime<*>, loc: ILocation): Branch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getHour(gmtJulDay, loc)
  }

  /**
   * @param gmtJulDay GMT 時間
   * @param loc 地點
   * @param eb 下一個地支
   * @return 下一個地支的開始時刻
   */
  fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch): GmtJulDay


  /**
   * @param lmt 傳入當地手錶時間
   * @param loc 當地的經緯度等資料
   * @param eb 欲求之下一個地支開始時刻
   * @return 回傳 LMT 時刻
   */
  fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver): ChronoLocalDateTime<*> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val resultGmtJulDay = getGmtNextStartOf(gmtJulDay, loc, eb)

    val resultGmt = julDayResolver.getLocalDateTime(resultGmtJulDay)
    return TimeTools.getLmtFromGmt(resultGmt, loc)
  }

  /**
   * 取得「前一個」此地支的開始時刻
   */
  fun getGmtPrevStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch): GmtJulDay

  fun getLmtPrevStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, julDayResolver: JulDayResolver): ChronoLocalDateTime<*> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val resultGmtJulDay = getGmtPrevStartOf(gmtJulDay, loc, eb)

    val resultGmt = julDayResolver.getLocalDateTime(resultGmtJulDay)
    return TimeTools.getLmtFromGmt(resultGmt, loc)
  }

  /**
   * accessory function , 傳回當地，一日內的時辰「開始」時刻
   */
  fun getDailyBranchStartMap(day: ChronoLocalDate, loc: ILocation, julDayResolver: JulDayResolver): Map<Branch, ChronoLocalDateTime<*>> {
    val lmtStart = day.atTime(LocalTime.MIDNIGHT)

    return Branch.values().map { b ->
      val lmt = if (b == Branch.子) {
        getLmtNextStartOf(lmtStart.minus(2, ChronoUnit.HOURS), loc, b, julDayResolver)
      } else {
        getLmtNextStartOf(lmtStart, loc, b, julDayResolver)
      }
      b to lmt
    }.sortedBy { (_, lmt) -> lmt }.toMap()
  }

  /**
   * accessory function , 傳回當地，一日內的時辰「中間」時刻
   */
  fun getDailyBranchMiddleMap(day: ChronoLocalDate, loc: ILocation, resolver: JulDayResolver): Map<Branch, ChronoLocalDateTime<*>> {

    val startTimeMap = getDailyBranchStartMap(day, loc, resolver)

    return startTimeMap.map { (branch, startTime) ->
      val endTime = if (branch != Branch.亥) {
        startTimeMap[branch.next]
      } else {
        val start: ChronoLocalDateTime<*> = startTimeMap[branch] ?: error("")
        getLmtNextStartOf(start, loc, Branch.子, resolver)
      }
      branch to startTime.plus(Duration.between(startTime, endTime).dividedBy(2))
    }.sortedBy { (_, lmt) -> lmt }.toMap()
  }

  /**
   * 從目前的時刻，取得下一個（或上一個）時辰中點的 LMT 時刻為何
   * @param next true = 下一個時辰 , false = 上一個時辰
   */
  fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, next: Boolean = true, julDayResolver: JulDayResolver): ChronoLocalDateTime<*>
}
