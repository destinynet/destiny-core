/**
 * Created by smallufo on 2023-08-05.
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import java.util.*


interface IEventSpanService {

  fun getEvents(stars: Set<Star>, year: Int, tzid: String, locale: Locale): List<IStarEventSpanDescription>

  fun getEvents(stars: Set<Star>, year: Int, month: Int, tzid: String, loc: ILocation, locale: Locale): List<IStarEventSpanDescription>
}
