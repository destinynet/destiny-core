/**
 * Created by smallufo on 2025-06-28.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation


interface IEventsTraversal {

  fun traverse(
    model: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    grain: BirthDataGrain,
    config: AstrologyTraversalConfig,
    outerPoints: Set<AstroPoint> = model.points,
    innerPoints: Set<AstroPoint> = model.points,
  ): Sequence<AstroEventDto>

}

