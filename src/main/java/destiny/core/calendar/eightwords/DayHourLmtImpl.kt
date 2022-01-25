/**
 * Created by smallufo on 2019-05-03.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver

class DayHourLmtImpl(override val changeDayAfterZi: Boolean,
                     override val midnightImpl: IMidnight,
                     hourImpl: HourLmtImpl,
                     julDayResolver: JulDayResolver) : AbstractDayHourImpl(hourImpl, julDayResolver) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DayHourLmtImpl) return false

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
