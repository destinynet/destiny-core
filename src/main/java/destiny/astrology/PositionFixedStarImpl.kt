/**
 * Created by smallufo on 2017-07-08.
 */
package destiny.astrology

import destiny.core.calendar.ILocation

open class PositionFixedStarImpl internal constructor(fixedStar: FixedStar) :
  AbstractPositionImpl<FixedStar>(fixedStar) {

  override fun getPosition(gmtJulDay: Double,
                           loc: ILocation,
                           centric: Centric,
                           coordinate: Coordinate,
                           starPositionImpl: IStarPosition<*>): IPos {
    return starPositionImpl.getPosition(point , gmtJulDay , loc.lng , loc.lat , loc.altitudeMeter?:0.0 , centric , coordinate , 0.0 , 1013.25)
  }

}
