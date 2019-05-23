/**
 * Created by smallufo on 2017-07-08.
 */
package destiny.astrology

import destiny.core.calendar.ILocation

open class PositionFixedStarImpl(fixedStar: FixedStar) :
  AbstractPositionImpl<FixedStar>(fixedStar) {

  override fun getPosition(gmtJulDay: Double,
                           loc: ILocation,
                           centric: Centric,
                           coordinate: Coordinate,
                           starPositionImpl: IStarPosition<*>,
                           houseCuspImpl: IHouseCusp): IPos {
    return starPositionImpl.getPosition(point, gmtJulDay, loc.lat, loc.lng, loc.altitudeMeter?:0.0, centric, coordinate, 0.0, 1013.25)
  }

}
