/**
 * Created by smallufo on 2024-07-01.
 */
package destiny.core

import destiny.core.astrology.IHoroscopePresentConfig
import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.LocalDate


interface INowLoc : Serializable {
  val bdnp: IBirthDataNamePlace
  val localDate: LocalDate
  val location: ILocation
  val place: String?
}

data class NowLoc(
  override val bdnp: IBirthDataNamePlace,
  override val localDate: LocalDate,
  override val location: ILocation = bdnp.location,
  override val place: String? = bdnp.place,
) : INowLoc


interface IDailyHoroscope : INowLoc {
  val config: IHoroscopePresentConfig
}

data class DailyHoroscope(val daily: NowLoc, override val config: IHoroscopePresentConfig) : IDailyHoroscope, INowLoc by daily


interface IMonthlyHoroscope : INowLoc {
  val config: IHoroscopePresentConfig
}

data class MonthlyHoroscope(val nowLoc: NowLoc, override val config: IHoroscopePresentConfig) : IMonthlyHoroscope, INowLoc by nowLoc
