/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable

@Serializable
data class FortuneLargeConfig(val impl: Impl = Impl.DefaultSpan,
                              val span : Double = 120.0): java.io.Serializable {
  enum class Impl {
    DefaultSpan,    // 傳統、標準大運 (每柱十年)
    SolarTermsSpan  // 節氣星座過運法 (每柱五年)
  }
}

@DestinyMarker
class FortuneLargeConfigBuilder : Builder<FortuneLargeConfig> {

  var impl: FortuneLargeConfig.Impl = FortuneLargeConfig.Impl.DefaultSpan
  var span : Double = 120.0

  override fun build(): FortuneLargeConfig {
    return FortuneLargeConfig(impl, span)
  }

  companion object {
    fun fortuneLarge(block: FortuneLargeConfigBuilder.() -> Unit = {}) : FortuneLargeConfig {
      return FortuneLargeConfigBuilder().apply(block).build()
    }
  }
}

class FortuneLargeFeature(private val implMap : Map<FortuneLargeConfig.Impl, IPersonFortuneLarge>,
                          private val julDayResolver: JulDayResolver) : PersonFeature<FortuneLargeConfig , List<FortuneData>> {
  override val key: String = "fortuneLargeFeature"

  override val defaultConfig: FortuneLargeConfig = FortuneLargeConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: FortuneLargeConfig): List<FortuneData> {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val count = when (config.impl) {
      FortuneLargeConfig.Impl.DefaultSpan    -> 9
      FortuneLargeConfig.Impl.SolarTermsSpan -> 18
    }

    return implMap[config.impl]!!.getFortuneDataList(lmt, loc, gender, count)
  }
}
