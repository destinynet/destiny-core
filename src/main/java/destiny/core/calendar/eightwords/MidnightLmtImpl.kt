/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:53:20
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.*
import java.time.temporal.ChronoUnit

/**
 * 純粹以地方平均時（手錶時間）來判定
 */
class MidnightLmtImpl(private val julDayResolver: JulDayResolver) : IMidnight, Serializable {

  override fun getNextMidnight(gmtJulDay: GmtJulDay, loc: ILocation): GmtJulDay {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val resultLmt = lmt.plus(1, ChronoUnit.DAYS)
      .with(HOUR_OF_DAY, 0)
      .with(MINUTE_OF_HOUR, 0)
      .with(SECOND_OF_MINUTE, 0)
      .with(NANO_OF_SECOND, 0)
    val resultGmt = TimeTools.getGmtFromLmt(resultLmt, loc)
    return TimeTools.getGmtJulDay(resultGmt)
  }


  override fun getNextMidnight(lmt: ChronoLocalDateTime<*>,
                               loc: ILocation,
                               julDayResolver: JulDayResolver): ChronoLocalDateTime<*> {
    return lmt
      .plus(1, ChronoUnit.DAYS)
      .with(HOUR_OF_DAY, 0)
      .with(MINUTE_OF_HOUR, 0)
      .with(SECOND_OF_MINUTE, 0)
      .with(NANO_OF_SECOND, 0)
  }
}
