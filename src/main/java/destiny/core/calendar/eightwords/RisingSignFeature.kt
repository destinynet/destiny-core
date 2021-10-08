/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.astrology.HouseConfig
import destiny.core.astrology.HouseConfigBuilder
import destiny.core.astrology.IHouseCuspFeature
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import java.util.*
import javax.inject.Named


@Serializable
data class RisingSignConfig(
  val houseConfig: HouseConfig = HouseConfig(),
  val tradChineseRisingSignConfig: TradChineseRisingSignConfig = TradChineseRisingSignConfig(),
  val impl: Impl = Impl.HouseCusp
): java.io.Serializable {
  enum class Impl {
    HouseCusp,
    TradChinese
  }
}

fun RisingSignConfig.Impl.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      RisingSignConfig.Impl.HouseCusp   -> "真實星體觀測"
      RisingSignConfig.Impl.TradChinese -> "傳統推命宮法"
    }
  }
}

fun RisingSignConfig.Impl.toString(locale: Locale): String {
  return this.asDescriptive().toString(locale)
}


@DestinyMarker
class RisingSignConfigBuilder : Builder<RisingSignConfig> {

  var impl: RisingSignConfig.Impl = RisingSignConfig.Impl.HouseCusp

  private var houseConfig: HouseConfig = HouseConfig()
  fun houseCusp(block: HouseConfigBuilder.() -> Unit = {}) {
    houseConfig = HouseConfigBuilder.houseCusp(block)
    impl = RisingSignConfig.Impl.HouseCusp
  }

  @DestinyMarker
  class TradChineseRisingSignConfigBuilder : Builder<TradChineseRisingSignConfig> {

    var hourImpl : HourBranchConfig.HourImpl = HourBranchConfig.HourImpl.TST

    override fun build(): TradChineseRisingSignConfig {
      return TradChineseRisingSignConfig(hourImpl)
    }
  }

  private var tradChineseRisingSignConfig: TradChineseRisingSignConfig = TradChineseRisingSignConfig()
  fun tradChinese(block: TradChineseRisingSignConfigBuilder.() -> Unit = {}) {
    tradChineseRisingSignConfig = TradChineseRisingSignConfigBuilder().apply(block).build()
    impl = RisingSignConfig.Impl.TradChinese
  }

  override fun build(): RisingSignConfig {
    return RisingSignConfig(houseConfig, tradChineseRisingSignConfig, impl)
  }

  companion object {
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
    return when (config.impl) {
      RisingSignConfig.Impl.HouseCusp   -> houseCuspFeature.getRisingSign(gmtJulDay, loc, config.houseConfig)
      RisingSignConfig.Impl.TradChinese -> tradChineseRisingSignFeature.getModel(gmtJulDay, loc, config.tradChineseRisingSignConfig)
    }
  }
}
