/**
 * Created by smallufo on 2021-08-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.Feature
import java.time.chrono.ChronoLocalDateTime


/** 取得下一個子正的時刻 */
class MidnightFeature(private val midnightImplMap: Map<DayConfig.MidnightImpl, IMidnight>,
                      private val julDayResolver: JulDayResolver) : Feature<DayConfig, GmtJulDay> {

  override val key: String = "midnight"

  override val defaultConfig: DayConfig = DayConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayConfig): GmtJulDay {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc , julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: DayConfig): GmtJulDay {
    val resultLmt = midnightImplMap[config.midnight]!!.getNextMidnight(lmt, loc, julDayResolver)
    return TimeTools.getGmtJulDay(resultLmt, loc)
  }
}
