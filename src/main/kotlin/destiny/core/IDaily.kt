/**
 * Created by smallufo on 2024-07-01.
 */
package destiny.core

import destiny.core.astrology.IHoroscopePresentConfig
import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.LocalDate


interface IDaily : Serializable {
  val bdnp: IBirthDataNamePlace
  val localDate: LocalDate
  val location: ILocation
}

data class Daily(override val bdnp: IBirthDataNamePlace,
                 override val localDate: LocalDate,
                 override val location: ILocation) : IDaily


interface IDailyHoroscope : IDaily {
  val config : IHoroscopePresentConfig
}

data class DailyHoroscope(val daily: Daily, override val config: IHoroscopePresentConfig) : IDailyHoroscope , IDaily by daily
