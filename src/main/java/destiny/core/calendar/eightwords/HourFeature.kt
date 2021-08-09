/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IRiseTrans
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.chinese.Branch
import destiny.tools.Builder
import destiny.tools.Feature


data class HourConfig(
  val dayConfig: DayConfig = DayConfig(),
  val impl: Impl = Impl.TST
) {
  enum class Impl {
    TST,
    LMT
  }
}

class HourConfigBuilder : Builder<HourConfig> {
  var dayConfig = DayConfig()
  fun dayConfig(block: DayConfigBuilder.() -> Unit = {}) {
    this.dayConfig = DayConfigBuilder().apply(block).build()
  }

  var impl = HourConfig.Impl.TST

  override fun build(): HourConfig {
    return HourConfig(dayConfig, impl)
  }
}

fun hourConfig(block: HourConfigBuilder.() -> Unit = {}): HourConfig {
  return HourConfigBuilder().apply(block).build()
}

class HourFeature(private val riseTransImpl: IRiseTrans ,
                  private val julDayResolver: JulDayResolver) : Feature<HourConfig, Branch> {

  override val key: String = "hour"

  override val defaultConfig: HourConfig = HourConfig()

  override val builder: Builder<HourConfig> = HourConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HourConfig): Branch {
    return when(config.impl) {
      HourConfig.Impl.TST -> {
        getHourTst(gmtJulDay, loc, riseTransImpl)
      }
      HourConfig.Impl.LMT -> {
        getHourLmtByGmtJulDay(gmtJulDay, loc, julDayResolver)
      }
    }
  }
}
