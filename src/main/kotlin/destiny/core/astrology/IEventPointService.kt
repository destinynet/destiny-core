/**
 * Created by smallufo on 2023-12-08.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.IEvent
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools.toGmtJulDay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId


interface IEventPointService {

  /** 某範圍內的事件 : 節氣、星體逆行、空亡、日蝕、月蝕 */
  fun getEvents(
    planets: Set<Planet>,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    loc: ILocation,
    phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)
  ): List<IEvent>

  /** 月份事件 : 節氣、逆行、月空亡、日蝕、月蝕 */
  fun getMonthlyEvents(planets: Set<Planet>, year: Int, month: Int, loc: ILocation, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent> {
    val zoneId = ZoneId.of(loc.tzid)
    val monthStart = LocalDateTime.of(year, month, 1, 0, 0, 0)
    val fromGmt = monthStart.toGmtJulDay(zoneId)
    val toGmt = monthStart.plusMonths(1).toGmtJulDay(zoneId)

    return getEvents(planets, fromGmt, toGmt, loc, phases)
  }

  /** 當日事件 : 節氣、星體逆行、空亡、日蝕、月蝕 */
  fun getDailyEvents(stars: Set<Planet>, localDate: LocalDate, loc: ILocation, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent> {
    val zoneId = ZoneId.of(loc.tzid)

    val fromGmt = localDate.atStartOfDay().toGmtJulDay(zoneId)
    val toGmt = localDate.plusDays(1).atStartOfDay().toGmtJulDay(zoneId)
    return getEvents(stars, fromGmt, toGmt, loc, phases)
  }
}
