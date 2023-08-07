/**
 * Created by smallufo on 2023-08-05.
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import java.time.ZoneId
import java.util.*


interface IEventSpanService {

  fun getYearlyEvents(stars: Set<Star>, year: Int, zoneId: ZoneId, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarLocalEventSpan>

  fun getMonthlyEvents(stars: Set<Star>, year: Int, month: Int, loc: ILocation, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarLocalEventSpan>
}
