/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import destiny.core.calendar.Location

import java.time.chrono.ChronoLocalDateTime

interface ClockwiseIF : Descriptive {

  fun getClockwise(lmt: ChronoLocalDateTime<*>, loc: Location): Clockwise
}
