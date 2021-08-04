/**
 * Created by smallufo on 2017-11-03.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.Location

import java.io.Serializable

/**
 * 描述一段期間，其 Planetary Hour 屬於哪顆行星 的資料結構
 */
data class PlanetaryHour(
  /** GMT julDay  */
  val hourStart: GmtJulDay,

  /** GMT julDay  */
  val hourEnd: GmtJulDay,

  /** 此時刻，是白天，還是黑夜  */
  val dayNight: DayNight,

  val planet: Planet,

  val location: Location) : Serializable
