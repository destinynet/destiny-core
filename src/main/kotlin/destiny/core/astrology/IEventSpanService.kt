/**
 * Created by smallufo on 2023-08-05.
 */
package destiny.core.astrology

import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.astrology.eclipse.EclipseTime
import destiny.core.calendar.*
import destiny.core.calendar.TimeTools.toGmtJulDay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId


interface IEventSpanService {

  /** 年度事件 : 節氣、逆行、日蝕、月蝕  (無空亡) */
  fun getYearlyEvents(stars: Set<Star>, year: Int, zoneId: ZoneId, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent>

  /** 某範圍內的事件 : 節氣、星體逆行、空亡、日蝕、月蝕 */
  fun getRangeEventSpans(stars: Set<Star>, fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent>

  /** 月份事件 : 節氣、逆行、月空亡、日蝕、月蝕 */
  fun getMonthlyEventSpans(stars: Set<Star>, year: Int, month: Int, loc: ILocation, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent> {
    val zoneId = ZoneId.of(loc.tzid)
    val monthStart = LocalDateTime.of(year, month, 1, 0, 0, 0)
    val fromGmt = monthStart.toGmtJulDay(zoneId)
    val toGmt = monthStart.plusMonths(1).toGmtJulDay(zoneId)

    return getRangeEventSpans(stars, fromGmt, toGmt, loc, phases)
  }

  /** 當日事件 : 節氣、星體逆行、空亡、日蝕、月蝕 */
  fun getDailyEventSpans(stars: Set<Star>, localDate: LocalDate, loc: ILocation, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent> {
    val zoneId = ZoneId.of(loc.tzid)

    val fromGmt = localDate.atStartOfDay().toGmtJulDay(zoneId)
    val toGmt = localDate.plusDays(1).atStartOfDay().toGmtJulDay(zoneId)
    return getRangeEventSpans(stars, fromGmt, toGmt, loc, phases)
  }

  /**
   * 內容
   * 包含三年的星體逆行資料
   * 最近三個月的星體空亡資料
   */
  fun getThisYearEvents(stars: Set<Star>, loc: ILocation, phases: Set<RetrogradePhase>): List<IEvent>


  fun List<IEvent>.toTimeDesc(fromGmt: GmtJulDay, toGmtJulDay: GmtJulDay): List<TimeDesc> {
    return this.flatMap { event: IEvent ->
      when (event) {
        // 節氣
        is SolarTermsEvent      -> setOf<TimeDesc>(TimeDesc.TypeSolarTerms(event.begin, event.solarTerms.toString(), event.solarTerms))
        // 日蝕
        is AbstractSolarEclipse -> {
          buildSet {
            if (event.begin in fromGmt..toGmtJulDay) {
              add(TimeDesc.TypeSolarEclipse(event.begin, event.solarType, EclipseTime.BEGIN))
            }
            if (event.max in fromGmt..toGmtJulDay) {
              add(TimeDesc.TypeSolarEclipse(event.max, event.solarType, EclipseTime.MAX))
            }
            if (event.end in fromGmt..toGmtJulDay) {
              add(TimeDesc.TypeSolarEclipse(event.end, event.solarType, EclipseTime.END))
            }
          }
        }

        // 月蝕
        is AbstractLunarEclipse -> {
          buildSet {
            if (event.begin in fromGmt..toGmtJulDay) {
              add(TimeDesc.TypeLunarEclipse(event.begin, event.lunarType, EclipseTime.BEGIN))
            }
            if (event.max in fromGmt..toGmtJulDay) {
              add(TimeDesc.TypeLunarEclipse(event.max, event.lunarType, EclipseTime.MAX))
            }
            if (event.end in fromGmt..toGmtJulDay) {
              add(TimeDesc.TypeLunarEclipse(event.end, event.lunarType, EclipseTime.END))
            }
          }
        }

        else                    -> emptySet()
      }
    }.sortedBy { it }
      .toList()
  }
}
