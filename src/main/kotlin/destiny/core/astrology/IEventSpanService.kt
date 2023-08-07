/**
 * Created by smallufo on 2023-08-05.
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


interface IEventSpanService {

  fun getYearlyEvents(stars: Set<Star>, year: Int, zoneId: ZoneId, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarLocalEventSpan>

  fun getMonthlyEvents(stars: Set<Star>, year: Int, month: Int, loc: ILocation, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarLocalEventSpan>

  fun getThisYearEvents(stars: Set<Star>, zoneId: ZoneId, locale: Locale, phases: Set<RetrogradePhase>) : List<IStarLocalEventSpan> {
    val thisYear = LocalDateTime.now().year

    return buildSet {
      addAll(getYearlyEvents(stars, thisYear - 1, zoneId, locale, phases))
      addAll(getYearlyEvents(stars, thisYear, zoneId, locale, phases))
      addAll(getYearlyEvents(stars, thisYear + 1, zoneId, locale, phases))
    }.toSortedSet(eventSpanComparator).toList()
  }

  companion object {
    // events 年份頭尾會重複events , 只針對 gmtJulDay 整數部分比對，以過濾重複的 events
    val eventSpanComparator = Comparator<IStarLocalEventSpan> { o1, o2 ->
      if (o1.star == o2.star && o1.duration.inWholeDays == o2.duration.inWholeDays) {
        o1.begin.value.toInt() - o2.begin.value.toInt()
      } else {
        o1.compareTo(o2)
      }
    }
  }
}
