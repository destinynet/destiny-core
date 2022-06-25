/**
 * Created by smallufo on 2019-05-02.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver

class DayHourSolarTransImpl(override val changeDayAfterZi: Boolean,
                            override val midnightImpl: IMidnight,
                            hourImpl: HourSolarTransImpl,
                            julDayResolver: JulDayResolver) : AbstractDayHourImpl(hourImpl, julDayResolver)
