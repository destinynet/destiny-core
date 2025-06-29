/**
 * Created by smallufo on 2025-06-28.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation


interface IEventsTraversal {

  fun traverse(
    inner: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    includeHour: Boolean,
    config: AstrologyTraversalConfig
  ): Sequence<AstroEventDto>

}

