/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:53:20
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Function

/**
 * 純粹以地方平均時（手錶時間）來判定
 */
class MidnightLmtImpl : IMidnight, Serializable {

  override fun getNextMidnight(gmtJulDay: Double, loc: Location): Double {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, revJulDayFunc)

    val resultLmt = lmt.plus(1, ChronoUnit.DAYS)
      .with(HOUR_OF_DAY, 0)
      .with(MINUTE_OF_HOUR, 0)
      .with(SECOND_OF_MINUTE, 0)
      .with(NANO_OF_SECOND, 0)
    val resultGmt = TimeTools.getGmtFromLmt(resultLmt, loc)
    return TimeTools.getGmtJulDay(resultGmt)
  }


  override fun getNextMidnight(lmt: ChronoLocalDateTime<*>, loc: Location, revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    return lmt
      .plus(1, ChronoUnit.DAYS)
      .with(HOUR_OF_DAY, 0)
      .with(MINUTE_OF_HOUR, 0)
      .with(SECOND_OF_MINUTE, 0)
      .with(NANO_OF_SECOND, 0)
  }

  override fun getTitle(locale: Locale): String {
    return "以地方平均時（ LMT）來判定"
  }

  override fun getDescription(locale: Locale): String {
    return "晚上零時就是子正，不校正經度差以及真太陽時"
  }

  companion object {

    private val revJulDayFunc = Function<Double, ChronoLocalDateTime<*>> { JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
