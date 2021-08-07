package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import destiny.tools.Feature
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime

class DayConfig(var changeDayAfterZi: Boolean = true)

interface IDayProcessor {
  fun getDayModel(gmtJulDay: GmtJulDay, location: ILocation, dayConfig: DayConfig): StemBranch
}


class DayFeature(
  private val defaultConfig: DayConfig,
  private val dayProcessor: IDayProcessor
) : Feature<DayConfig, IDayProcessor, StemBranch> {

  override val key: String = "day"

  override fun IDayProcessor.getModel(gmtJulDay: GmtJulDay, loc: ILocation, block: DayConfig.() -> Unit): StemBranch {
    val cfg = defaultConfig.apply(block)
    return this.getDayModel(gmtJulDay, loc, cfg)
  }
}


class DayHourProcessor(
  val hourImpl: IHour,
  val midnightImpl: IMidnight,
  val julDayResolver: JulDayResolver
) : IDayProcessor {

  override fun getDayModel(gmtJulDay: GmtJulDay, location: ILocation, dayConfig: DayConfig): StemBranch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, location, julDayResolver)

    return getDay(lmt, location, dayConfig.changeDayAfterZi)
  }


  private fun getDay(lmt: ChronoLocalDateTime<*>, location: ILocation, changeDayAfterZi: Boolean): StemBranch {

    return getDay(lmt, location, hourImpl, midnightImpl, changeDayAfterZi, julDayResolver)
  } // LMT 版本

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
