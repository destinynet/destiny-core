/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

interface IPosition<out T : Point> {

  val point: T

  fun getPosition(lmt: ChronoLocalDateTime<*>,
                  loc: ILocation,
                  centric: Centric,
                  coordinate: Coordinate,
                  starPositionImpl: IStarPosition<*>): IPos
}
