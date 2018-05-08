/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology

import destiny.core.calendar.ILocation

abstract class PositionStarImpl internal constructor(star: Star) : AbstractPositionImpl<Star>(star) {

  override fun getPosition(gmtJulDay: Double,
                           loc: ILocation,
                           centric: Centric,
                           coordinate: Coordinate,
                           starPositionImpl: IStarPosition<*>): IPos {
    return starPositionImpl.getPosition(point , gmtJulDay , loc.lng , loc.lat , loc.altitudeMeter?:0.0 , centric , coordinate , 0.0 , 1013.25)
  }

}
