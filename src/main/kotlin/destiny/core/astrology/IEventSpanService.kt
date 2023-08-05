/**
 * Created by smallufo on 2023-08-05.
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import java.util.*


interface IEventSpanService {

  fun getEvents(stars: Set<Star>, year: Int, tzid: String, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarLocalEventSpan>

  fun getEvents(stars: Set<Star>, year: Int, month: Int, loc: ILocation, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarLocalEventSpan>
}
