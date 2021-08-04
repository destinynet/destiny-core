/**
 * Created by smallufo on 2017-07-09.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

open class PositionLunarPointImpl(val starPositionImpl: IStarPosition<*>, lunarPoint: LunarPoint) :
  AbstractPositionImpl<LunarPoint>(lunarPoint) {

  override fun getPosition(gmtJulDay: GmtJulDay,
                           loc: ILocation,
                           centric: Centric,
                           coordinate: Coordinate,
                           temperature: Double,
                           pressure: Double): IPos {
    return starPositionImpl.getPosition(point, gmtJulDay, loc.lat, loc.lng, loc.altitudeMeter?:0.0, centric, coordinate , temperature, pressure)
  }
}
