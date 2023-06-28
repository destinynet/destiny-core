/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese

import destiny.core.Clockwise
import destiny.core.Descriptive
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

interface IClockwise : Descriptive {

  fun getClockwise(gmtJulDay: GmtJulDay, loc: ILocation): Clockwise

}
