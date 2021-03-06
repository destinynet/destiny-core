/**
 * Created by smallufo on 2019-05-02.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver


class DayHourSolarTransImpl(override val changeDayAfterZi: Boolean,
                            override val midnightImpl: IMidnight,
                            hourImpl: HourSolarTransImpl,
                            julDayResolver: JulDayResolver) : AbstractDayHourImpl(hourImpl, julDayResolver) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DayHourSolarTransImpl) return false

    if (changeDayAfterZi != other.changeDayAfterZi) return false
    if (midnightImpl != other.midnightImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = changeDayAfterZi.hashCode()
    result = 31 * result + midnightImpl.hashCode()
    return result
  }
}
