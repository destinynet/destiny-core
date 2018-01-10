/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.core.calendar.Location
import destiny.core.chinese.Clockwise
import destiny.core.chinese.IClockwise
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/**
 * 推算貴神
 * 固定「晝順夜逆」
 * Clockwise Day Clockwise / Night Counter
 * dayNightFixedImpl
 */
class ClockwiseDayNightFixedImpl(private val differentiator: DayNightDifferentiator) : IClockwise, Serializable {

  override fun getClockwise(lmt: ChronoLocalDateTime<*>, loc: Location): Clockwise {
    val dayNight = differentiator.getDayNight(lmt, loc)
    return if (dayNight == DayNight.DAY) Clockwise.CLOCKWISE else Clockwise.COUNTER
  }

  override fun getTitle(locale: Locale): String {
    return "晝順夜逆"
  }

  override fun getDescription(locale: Locale): String {
    return "固定為晝順夜逆"
  }
}
