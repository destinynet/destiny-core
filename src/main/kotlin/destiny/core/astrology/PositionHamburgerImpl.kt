/**
 * Created by smallufo on 2017-07-08.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

open class PositionHamburgerImpl(val starPositionImpl: IStarPosition<*> , hamburger: Hamburger) :
  AbstractPositionImpl<Hamburger>(hamburger) {

  override fun getPosition(gmtJulDay: GmtJulDay,
                           loc: ILocation,
                           centric: Centric,
                           coordinate: Coordinate,
                           temperature: Double,
                           pressure: Double): IPos {
    return starPositionImpl.getPosition(point, gmtJulDay, loc, centric, coordinate , temperature, pressure)
  }
}
