/**
 * Created by smallufo on 2017-11-03.
 */
package destiny.astrology

import destiny.core.calendar.Location

import java.io.Serializable

/**
 * 描述一段期間，其 Planetary Hour 屬於哪顆行星 的資料結構
 */
data class PlanetaryHour(
  /** GMT julDay  */
  val hourStart: Double,

  /** GMT julDay  */
  val hourEnd: Double,

  /** 此時刻，是白天，還是黑夜  */
  val dayNight: DayNight,

  val planet: Planet,

  val location: Location) : Serializable
