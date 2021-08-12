/**
 * Created by smallufo on 2021-08-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IRiseTrans
import destiny.core.astrology.Planet
import destiny.core.astrology.TransPoint
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.Builder
import destiny.tools.Feature
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit


class MidnightFeature(private val riseTransImpl: IRiseTrans,
                      private val julDayResolver: JulDayResolver) : Feature<DayConfig, GmtJulDay> {

  override val key: String = "midnight"

  override val defaultConfig: DayConfig = DayConfig()

  override val builder: Builder<DayConfig> = DayConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayConfig): GmtJulDay {

    return when (config.midnight) {
      DayConfig.MidnightImpl.NADIR  -> riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, loc)!!
      DayConfig.MidnightImpl.CLOCK0 -> {
        val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

        val resultLmt = lmt.plus(1, ChronoUnit.DAYS)
          .with(ChronoField.HOUR_OF_DAY, 0)
          .with(ChronoField.MINUTE_OF_HOUR, 0)
          .with(ChronoField.SECOND_OF_MINUTE, 0)
          .with(ChronoField.NANO_OF_SECOND, 0)
        val resultGmt = TimeTools.getGmtFromLmt(resultLmt, loc)
        return TimeTools.getGmtJulDay(resultGmt)
      }
    }
  }


}
