/**
 * Created by smallufo on 2021-08-06.
 */
package destiny.tools

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation


interface Feature<out Config : Any, Processor : Any , Model : Any> {

  val key: String

  fun prepare(block: Config.() -> Unit = {}) : Processor

  fun Processor.getModel(gmtJulDay: GmtJulDay, loc: ILocation): Model
}
