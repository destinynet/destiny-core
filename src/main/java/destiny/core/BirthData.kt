/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core

import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.*

interface IBirthData {
  val gender: Gender
  val time: ChronoLocalDateTime<*>
  val location: ILocation

  val ad: Boolean
    get() = time.get(YEAR) > 0

  /** 西元年份，一定大於 0 */
  val year: Int
    get() = time.get(YEAR_OF_ERA)

  val month: Int
    get() = time.get(MONTH_OF_YEAR)

  val day: Int
    get() = time.get(DAY_OF_MONTH)

  val hour: Int
    get() = time.get(HOUR_OF_DAY)

  val minute: Int
    get() = time.get(MINUTE_OF_HOUR)

  val second: Double
    get() = time.get(SECOND_OF_MINUTE) + time.get(NANO_OF_SECOND) / 1_000_000_000.0
}

data class BirthData(
  override val gender: Gender,
  override val time: ChronoLocalDateTime<*>,
  override val location: ILocation
                    ) : IBirthData, Serializable


interface IBirthDataNamePlace : IBirthData {
  val name: String?
  val place: String?
}

data class BirthDataNamePlace(
  val birthData: BirthData,
  override val name: String?,
  override val place: String?) : IBirthDataNamePlace, IBirthData by birthData, Serializable {

  constructor(gender: Gender, time: ChronoLocalDateTime<*>, location: ILocation, name: String?, place: String?)
    : this(BirthData(gender, time, location), name, place)
}