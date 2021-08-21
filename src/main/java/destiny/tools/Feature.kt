/**
 * Created by smallufo on 2021-08-06.
 */
package destiny.tools

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime


interface Builder<Model> {

  fun build(): Model
}

interface Feature<out Config : Any, Model : Any?> {

  val key: String

  val defaultConfig: Config

  fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model

  fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config = defaultConfig): Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getModel(gmtJulDay, loc, config)
  }
}

interface PersonFeature<out Config : Any, Model> : Feature<Config, Model> {

  fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: @UnsafeVariance Config = defaultConfig): Model

  fun getModel(bdnp: IBirthDataNamePlace, config: @UnsafeVariance Config = defaultConfig): Model {
    return getModel(bdnp.gmtJulDay, bdnp.location, bdnp.gender, bdnp.name, bdnp.place, config)
  }

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config): Model {
    return getModel(gmtJulDay, loc, Gender.男, null, null, config)
  }
}
