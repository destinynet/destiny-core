/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core

import destiny.core.calendar.IDate
import destiny.core.calendar.ILocation
import destiny.core.calendar.ITime
import destiny.core.calendar.Location
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.*
import java.util.*

/** 一個命盤最基本的必備元素 : 性別 / 時間 / 地點  */
open class BirthData(override val gender: Gender,
                     override val time: ChronoLocalDateTime<*>,
                     // TODO : location 被 UI 端可能重新設定，暫時無法設成 val
                     var location: Location) : IGender, ITime, IDate, ILocation by location , Serializable {

  override val isAd: Boolean
    get() = time.get(YEAR) > 0

  override val year: Int
    get() = time.get(YEAR_OF_ERA)

  override val month: Int
    get() = time.get(MONTH_OF_YEAR)

  override val day: Int
    get() = time.get(DAY_OF_MONTH)

  val hour: Int
    get() = time.get(HOUR_OF_DAY)

  val minute: Int
    get() = time.get(MINUTE_OF_HOUR)

  val second: Double
    get() = time.get(SECOND_OF_MINUTE) + time.get(NANO_OF_SECOND) / 1_000_000_000.0

  override fun toString(): String {
    return "[BirthData " + "gender=" + gender + ", time=" + time + ", location=" + location.decimal + ']'.toString()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other)
      return true
    if (other !is BirthData)
      return false
    val birthData = other as BirthData?
    return gender === birthData!!.gender && time == birthData!!.time && location == birthData.location
  }

  override fun hashCode(): Int {
    return Objects.hash(gender, time, location)
  }
}
