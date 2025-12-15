/**
 * Created by smallufo on 2017-07-09.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

open class PositionAsteroidImpl(val starPositionImpl: IStarPosition<*>, asteroid: Asteroid) : AbstractPositionImpl<Asteroid>(asteroid) {
  override fun getPosition(
    gmtJulDay: GmtJulDay,
    loc: ILocation,
    centric: Centric,
    coordinate: Coordinate,
    temperature: Double,
    pressure: Double,
    starTypeOptions: StarTypeOptions
  ): IPos {
    return starPositionImpl.calculateWithAzimuth(point, gmtJulDay, loc, centric, coordinate, temperature, pressure)
  }
}
