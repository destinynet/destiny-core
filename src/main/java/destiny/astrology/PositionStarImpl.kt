/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

abstract class PositionStarImpl internal constructor(star: Star) : AbstractPositionImpl<Star>(star) {

  override fun getPosition(lmt: ChronoLocalDateTime<*>, loc: ILocation, centric: Centric, coordinate: Coordinate, starPositionImpl: IStarPosition<*>): IPos {
    return starPositionImpl.getPosition(point, lmt, loc, centric, coordinate)
  }
}
