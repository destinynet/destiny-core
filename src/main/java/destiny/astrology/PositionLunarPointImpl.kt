/**
 * Created by smallufo on 2017-07-09.
 */
package destiny.astrology

import destiny.core.calendar.ILocation

open class PositionLunarPointImpl(val starPositionImpl: IStarPosition<*>, lunarPoint: LunarPoint) :
  AbstractPositionImpl<LunarPoint>(lunarPoint) {

  override fun getPosition(gmtJulDay: Double,
                           loc: ILocation,
                           centric: Centric,
                           coordinate: Coordinate): IPos {
    return starPositionImpl.getPosition(point, gmtJulDay, loc.lat, loc.lng, loc.altitudeMeter?:0.0, centric, coordinate)
  }
}
