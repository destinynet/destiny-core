/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core

import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IBirthData {
  val gender: Gender
  val time: ChronoLocalDateTime<*>
  val location: ILocation
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