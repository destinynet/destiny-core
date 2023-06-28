/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.core.Clockwise
import destiny.core.DayNight
import destiny.core.Descriptive
import destiny.core.astrology.IDayNight
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.chinese.IClockwise
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 推算貴神
 * 固定「晝順夜逆」
 * Clockwise Day Clockwise / Night Counter
 * dayNightFixedImpl
 */
class ClockwiseDayNightFixedImpl(private val differentiator: IDayNight) : IClockwise,
                                                                          Descriptive by destiny.core.chinese.liuren.Clockwise.DayNightFixed.asDescriptive(),
                                                                          Serializable {

  override fun getClockwise(gmtJulDay: GmtJulDay, loc: ILocation): Clockwise {
    val dayNight = differentiator.getDayNight(gmtJulDay, loc)
    return if (dayNight == DayNight.DAY) Clockwise.CLOCKWISE else Clockwise.COUNTER
  }

  companion object {
    const val VALUE = "dayNightFixed"
  }
}
