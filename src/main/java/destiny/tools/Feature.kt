/**
 * Created by smallufo on 2021-08-06.
 */
package destiny.tools

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime


interface Feature<out Config : Any, Model : Any> {

  val key: String

  fun getModel(gmtJulDay: GmtJulDay, loc: ILocation , block: Config.() -> Unit = {}): Model

  fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation , block: Config.() -> Unit = {}): Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getModel(gmtJulDay, loc, block)
  }
}
