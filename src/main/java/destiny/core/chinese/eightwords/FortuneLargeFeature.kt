/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.tools.Builder
import destiny.tools.DestinyMarker

data class FortuneLargeConfig(val impl: Impl = Impl.DefaultSpan,
                              val span : Double = 120.0) {
  enum class Impl {
    DefaultSpan,    // default
    SolarTermsSpan  // 節氣星座過運法
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

class FortuneLargeFeature {
}
