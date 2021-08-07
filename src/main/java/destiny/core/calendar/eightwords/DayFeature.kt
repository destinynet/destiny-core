package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import destiny.tools.Feature

class DayConfig(var changeDayAfterZi: Boolean = true)

class DayFeature(
  private val defaultConfig: DayConfig,
  val hourImpl: IHour,
  val midnightImpl: IMidnight,
  val julDayResolver: JulDayResolver
) : Feature<DayConfig, StemBranch> {

  override val key: String = "day"

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, block: DayConfig.() -> Unit): StemBranch {
    val cfg = defaultConfig.apply(block)
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getDay(lmt, loc, hourImpl, midnightImpl, cfg.changeDayAfterZi, julDayResolver)
  }
}


