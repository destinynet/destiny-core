/**
 * Created by smallufo on 2020-01-04.
 */
package destiny.fengshui

import destiny.core.calendar.ILocation
import destiny.core.calendar.locationOf
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import java.util.*


data class CompassData(
  val time: ChronoLocalDateTime<*> = LocalDateTime.now(),
  val loc: ILocation = locationOf(Locale.TAIWAN),
  val width: Int = 640,
  val rotate: Double = 180.0,
  val zoom: Int = 15,
  // 不透明度 , 0.0 最透明
  val opaque: Float = 0.4f,
  // 放大倍數 1,2 or 4
  val scale: Int = 1
)
