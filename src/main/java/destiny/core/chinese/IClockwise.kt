/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

interface IClockwise : Descriptive {

  fun getClockwise(lmt: ChronoLocalDateTime<*>, loc: ILocation): Clockwise
}
