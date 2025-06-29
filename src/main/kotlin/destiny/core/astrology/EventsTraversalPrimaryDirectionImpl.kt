/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation


class EventsTraversalPrimaryDirectionImpl : IEventsTraversal {
  override fun traverse(
    inner: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    includeHour: Boolean,
    config: AstrologyTraversalConfig
  ): Sequence<AstroEventDto> {
    TODO("Not yet implemented")
  }
}
