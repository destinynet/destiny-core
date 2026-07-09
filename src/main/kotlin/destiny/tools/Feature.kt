/**
 * Created by smallufo on 2021-08-06.
 */
package destiny.tools

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime


interface Builder<Model> {

  fun build(): Model
}

interface Feature<out Config : Any, Model : Any?> {

  val key: String
    get() = javaClass.simpleName

  val defaultConfig: Config

  fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model

  fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model

}

