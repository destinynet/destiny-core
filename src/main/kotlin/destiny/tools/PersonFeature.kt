package destiny.tools

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

interface PersonFeature<out Config : Any, Model> : Feature<Config, Model> {

  fun getPersonModel(gmtJulDay: GmtJulDay,
                     loc: ILocation,
                     gender: Gender,
                     name: String?,
                     place: String?,
                     config: @UnsafeVariance Config = defaultConfig): Model

  fun getPersonModel(lmt: ChronoLocalDateTime<*>,
                     loc: ILocation,
                     gender: Gender,
                     name: String?,
                     place: String?,
                     config: @UnsafeVariance Config = defaultConfig): Model {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getPersonModel(gmtJulDay, loc, gender, name, place, config)
  }

  fun getPersonModel(bdnp: IBirthDataNamePlace, config: @UnsafeVariance Config = defaultConfig): Model {
    return getPersonModel(bdnp.gmtJulDay, bdnp.location, bdnp.gender, bdnp.name, bdnp.place, config)
  }

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: @UnsafeVariance Config): Model {
    return getPersonModel(gmtJulDay, loc, Gender.男, null, null, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: @UnsafeVariance Config): Model {
    return getPersonModel(lmt, loc, Gender.男, null, null , config)
  }
}
