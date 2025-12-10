/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDayNightConfig
import destiny.core.calendar.eightwords.ITransConfig
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import jakarta.inject.Named
import kotlinx.serialization.Serializable

enum class DayNightImpl {
  StarPos,  // 日升日落 : 太陽升起至落下，為晝；太陽落下至昇起，為夜
  Half,     // 前半天後半天 : 夜半子正至午正（前半天）為晝；中午至半夜（後半天）為夜 (極區內可能不適用)
  Simple    // 固定晝夜六點為界 : 僅能作為 Test 使用
}

@Serializable
data class DayNightConfig(override val dayNightImpl : DayNightImpl = DayNightImpl.StarPos,
                          /** for [DayNightImpl.StarPos] and [DayNightImpl.Half] */
                          override val transConfig: TransConfig = TransConfig()): IDayNightConfig,
                                                                                  ITransConfig by transConfig
@DestinyMarker
class DayNightConfigBuilder(val config : ITransConfig) : Builder<DayNightConfig> {

  var dayNightImpl : DayNightImpl = DayNightImpl.StarPos

  override fun build(): DayNightConfig {
    return DayNightConfig(dayNightImpl, config.transConfig)
  }

  companion object {
    context(config: ITransConfig)
    fun dayNight(block : DayNightConfigBuilder.() -> Unit = {}) : DayNightConfig {
      return DayNightConfigBuilder(config).apply(block).build()
    }
  }
}

@Named
class DayNightFeature(private val dayNightImplMap: Map<DayNightImpl, IDayNight>) : AbstractCachedFeature<IDayNightConfig, DayNight>() {

  override val key: String = "dayNight"

  override val defaultConfig: IDayNightConfig = DayNightConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: IDayNightConfig): DayNight {

    return dayNightImplMap[config.dayNightImpl]!!.getDayNight(gmtJulDay, loc, config.transConfig)
  }
}
