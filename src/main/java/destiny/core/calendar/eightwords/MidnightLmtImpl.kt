/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:53:20
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.*
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * 純粹以地方平均時（手錶時間）來判定
 */
@Impl([Domain(Domains.KEY_MIDNIGHT, MidnightLmtImpl.VALUE)])
class MidnightLmtImpl(private val julDayResolver: JulDayResolver) : IMidnight, Serializable {

  override fun getNextMidnight(gmtJulDay: Double, loc: ILocation): Double {
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

  override fun toString(locale: Locale): String {
    return "以地方平均時（LMT）夜半零時來判定"
  }

  override fun getDescription(locale: Locale): String {
    return "晚上零時就是子正，不校正經度差以及真太陽時"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


  companion object {
    const val VALUE: String = "lmt"
  }

}
