package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import destiny.tools.Feature

class DayConfig(var changeDayAfterZi: Boolean = true)

class DayFeature(
  val hourImpl: IHour,
  val midnightImpl: IMidnight,
  val julDayResolver: JulDayResolver
) : Feature<DayConfig, StemBranch> {

  override val key: String = "day"

  override val defaultConfig: DayConfig = DayConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayConfig): StemBranch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getDay(lmt, loc, hourImpl, midnightImpl, config.changeDayAfterZi, julDayResolver)
  }
}


