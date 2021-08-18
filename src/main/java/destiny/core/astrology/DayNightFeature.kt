/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.astrology.DayNightConfig.DayNightImpl
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.temporal.ChronoField

@Serializable
data class DayNightConfig(val impl : DayNightImpl = DayNightImpl.StarPos ,
                          /** for [DayNightImpl.StarPos] and [DayNightImpl.Half] */
                          val transConfig: TransConfig = TransConfig()) {
  enum class DayNightImpl {
    StarPos,  // 日升日落 : 太陽升起至落下，為晝；太陽落下至昇起，為夜
    Half,     // 前半天後半天 : 夜半子正至午正（前半天）為晝；中午至半夜（後半天）為夜 (極區內可能不適用)
    Simple    // 固定晝夜六點為界 : 僅能作為 Test 使用
  }
}

@DestinyMarker
class DayNightConfigBuilder : Builder<DayNightConfig> {

  var impl : DayNightConfig.DayNightImpl = DayNightConfig.DayNightImpl.StarPos

  var transConfig: TransConfig = TransConfig()

  fun trans(block : TransConfigBuilder.() -> Unit = {}) {
    transConfig = TransConfigBuilder.trans(block)
  }

  override fun build(): DayNightConfig {
    return DayNightConfig(impl, transConfig)
  }

  companion object {
    fun dayNight(block : DayNightConfigBuilder.() -> Unit = {}) : DayNightConfig {
      return DayNightConfigBuilder().apply(block).build()
    }
  }
}

class DayNightFeature(private val riseTransFeature: RiseTransFeature ,
                      private val julDayResolver: JulDayResolver) : Feature<DayNightConfig, DayNight> {

  override val key: String = "dayNight"

  override val defaultConfig: DayNightConfig = DayNightConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayNightConfig): DayNight {
    return when (config.impl) {
      DayNightConfig.DayNightImpl.StarPos -> {
        // 太陽升起至落下，為晝；太陽落下至昇起，為夜
        val nextSetting = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.SETTING, config.transConfig))!!
        val nextRising = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.RISING, config.transConfig))!!
        if (nextSetting > nextRising) {
          DayNight.NIGHT
        } else {
          DayNight.DAY
        }
      }
      DayNightConfig.DayNightImpl.Half    -> {
        val nextMeridian = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, config.transConfig))!!
        val nextNadir = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, config.transConfig))!!
        if (nextNadir > nextMeridian) {
          //子正到午正（上半天）
          DayNight.DAY
        } else {
          //午正到子正（下半天）
          DayNight.NIGHT
        }
      }
      DayNightConfig.DayNightImpl.Simple  -> {
        val lmt = TimeTools.getLmtFromGmt(julDayResolver.getLocalDateTime(gmtJulDay), loc)
        val hour = lmt.get(ChronoField.HOUR_OF_DAY)
        if (hour in 6..17)
          DayNight.DAY
        else
          DayNight.NIGHT
      }
    }
  }
}
