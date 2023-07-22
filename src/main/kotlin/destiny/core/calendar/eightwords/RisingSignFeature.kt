/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.HouseConfig
import destiny.core.astrology.IHouseConfig
import destiny.core.astrology.IHouseCuspFeature
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import jakarta.inject.Named
import kotlinx.serialization.Serializable

enum class RisingSignImpl {
  HouseCusp,
  TradChinese
}

@Serializable
data class RisingSignConfig(
  override val houseConfig: HouseConfig = HouseConfig(),
  override var tradChineseRisingSignConfig: TradChineseRisingSignConfig = TradChineseRisingSignConfig(),
  override var risingSignImpl: RisingSignImpl = RisingSignImpl.HouseCusp
): IRisingSignConfig , IHouseConfig by houseConfig

context(IHouseConfig)
@DestinyMarker
class RisingSignConfigBuilder : Builder<RisingSignConfig> {

  var risingSignImpl: RisingSignImpl = RisingSignImpl.HouseCusp

  @DestinyMarker
  class TradChineseRisingSignConfigBuilder : Builder<TradChineseRisingSignConfig> {

    var hourImpl : HourImpl = HourImpl.TST

    override fun build(): TradChineseRisingSignConfig {
      return TradChineseRisingSignConfig(hourImpl)
    }
  }

  private var tradChineseRisingSignConfig: TradChineseRisingSignConfig = TradChineseRisingSignConfig()
  fun tradChinese(block: TradChineseRisingSignConfigBuilder.() -> Unit = {}) {
    tradChineseRisingSignConfig = TradChineseRisingSignConfigBuilder().apply(block).build()
    risingSignImpl = RisingSignImpl.TradChinese
  }

  override fun build(): RisingSignConfig {
    return RisingSignConfig(houseConfig, tradChineseRisingSignConfig, risingSignImpl)
  }

  companion object {
    context(IHouseConfig)
    fun risingSign(block: RisingSignConfigBuilder.() -> Unit = {}): RisingSignConfig {
      return RisingSignConfigBuilder().apply(block).build()
    }
  }
}


@Named
class RisingSignFeature(private val houseCuspFeature: IHouseCuspFeature,
                        private val tradChineseRisingSignFeature: TradChineseRisingSignFeature
                        ): AbstractCachedFeature<RisingSignConfig, ZodiacSign>() {


  override val key: String = "risingSign"

  override val defaultConfig: RisingSignConfig = RisingSignConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: RisingSignConfig): ZodiacSign {
    return when (config.risingSignImpl) {
      RisingSignImpl.HouseCusp   -> houseCuspFeature.getRisingSign(gmtJulDay, loc, config.houseConfig)
      RisingSignImpl.TradChinese -> tradChineseRisingSignFeature.getModel(gmtJulDay, loc, config.tradChineseRisingSignConfig)
    }
  }
}
