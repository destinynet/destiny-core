package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import destiny.tools.Builder
import destiny.tools.Feature

class DayConfig(val changeDayAfterZi: Boolean = true)

class DayConfigBuilder : Builder<DayConfig> {
  var changeDayAfterZi: Boolean = true

  override fun build() : DayConfig {
    return DayConfig(changeDayAfterZi)
  }
}

fun dayConfig(block : DayConfigBuilder.() -> Unit = {}): DayConfig {
  return DayConfigBuilder().apply(block).build()
}


class DayFeature(
  val hourImpl: IHour,
  val midnightImpl: IMidnight,
  val julDayResolver: JulDayResolver
) : Feature<DayConfig, StemBranch> {

  override val key: String = "day"

  override val defaultConfig: DayConfig = DayConfig()

  override val builder: Builder<DayConfig> = DayConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayConfig): StemBranch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getDay(lmt, loc, hourImpl, midnightImpl, config.changeDayAfterZi, julDayResolver)
  }
}


