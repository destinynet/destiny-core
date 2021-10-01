/**
 * Created by smallufo on 2021-08-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.AbstractCachedFeature
import destiny.tools.Feature
import java.time.chrono.ChronoLocalDateTime
import kotlin.math.absoluteValue


interface IMidnightFeature : Feature<DayConfig, GmtJulDay> {

  fun getNextMidnight(gmtJulDay: GmtJulDay, loc: ILocation, impl: DayConfig.MidnightImpl): GmtJulDay

  fun getPrevMidnight(gmtJulDay: GmtJulDay, loc: ILocation, impl: DayConfig.MidnightImpl): GmtJulDay {
    val nextMidnight = getNextMidnight(gmtJulDay, loc, impl)

    return generateSequence(gmtJulDay - 0.5 to getNextMidnight(gmtJulDay - 0.5, loc, impl)) { pair ->
      pair.first to getNextMidnight(pair.first - 0.5, loc, impl)
    }.first { (it.second - nextMidnight).absoluteValue > 0.5 }
      .second
  }

}

/** 取得下一個子正的時刻 */
class MidnightFeature(private val midnightImplMap: Map<DayConfig.MidnightImpl, IMidnight>,
                      private val julDayResolver: JulDayResolver) : AbstractCachedFeature<DayConfig, GmtJulDay>() , IMidnightFeature {

  override val key: String = "midnight"

  override val defaultConfig: DayConfig = DayConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: DayConfig): GmtJulDay {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc , julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: DayConfig): GmtJulDay {
    val resultLmt = midnightImplMap[config.midnight]!!.getNextMidnight(lmt, loc, julDayResolver)
    return TimeTools.getGmtJulDay(resultLmt, loc)
  }

  override fun getNextMidnight(gmtJulDay: GmtJulDay, loc: ILocation, impl: DayConfig.MidnightImpl): GmtJulDay {
    return midnightImplMap[impl]!!.getNextMidnight(gmtJulDay, loc)
  }
}
