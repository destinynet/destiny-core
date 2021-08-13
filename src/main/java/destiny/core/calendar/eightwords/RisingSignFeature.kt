/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.Coordinate
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class RisingSignConfig(
  val houseCuspConfig: HouseCuspConfig = HouseCuspConfig(),
  val tradChineseRisingSignConfig: TradChineseRisingSignConfig = TradChineseRisingSignConfig(),
  val impl: Impl = Impl.HouseCusp
) {
  enum class Impl {
    HouseCusp,
    TradChinese
  }
}

@DestinyMarker
class RisingSignConfigBuilder : Builder<RisingSignConfig> {

  var impl: RisingSignConfig.Impl = RisingSignConfig.Impl.HouseCusp

  @DestinyMarker
  class HouseCuspConfigBuilder : Builder<HouseCuspConfig> {
    var houseSystem: HouseSystem = HouseSystem.PLACIDUS
    var coordinate: Coordinate = Coordinate.ECLIPTIC

    override fun build(): HouseCuspConfig {
      return HouseCuspConfig(houseSystem, coordinate)
    }

    companion object {
      fun houseCusp(block: HouseCuspConfigBuilder.() -> Unit = {}): HouseCuspConfig {
        return HouseCuspConfigBuilder().apply(block).build()
      }
    }
  }

  var houseCuspConfig: HouseCuspConfig = HouseCuspConfig()
  fun houseCusp(block: HouseCuspConfigBuilder.() -> Unit = {}) {
    houseCuspConfig = HouseCuspConfigBuilder().apply(block).build()
    impl = RisingSignConfig.Impl.HouseCusp
  }

  @DestinyMarker
  class TradChineseRisingSignConfigBuilder : Builder<TradChineseRisingSignConfig> {

    var hourImpl : DayHourConfig.HourImpl = DayHourConfig.HourImpl.TST

    override fun build(): TradChineseRisingSignConfig {
      return TradChineseRisingSignConfig(hourImpl)
    }
  }

  var tradChineseRisingSignConfig: TradChineseRisingSignConfig = TradChineseRisingSignConfig()
  fun tradChinese(block: TradChineseRisingSignConfigBuilder.() -> Unit = {}) {
    tradChineseRisingSignConfig = TradChineseRisingSignConfigBuilder().apply(block).build()
    impl = RisingSignConfig.Impl.TradChinese
  }

  override fun build(): RisingSignConfig {
    return RisingSignConfig(houseCuspConfig, tradChineseRisingSignConfig, impl)
  }

  companion object {
    fun risingSign(block: RisingSignConfigBuilder.() -> Unit = {}): RisingSignConfig {
      return RisingSignConfigBuilder().apply(block).build()
    }
  }
}


class RisingSignFeature(
  private val houseCuspFeature: HouseCuspFeature,
  private val tradChineseRisingSignFeature: TradChineseRisingSignFeature
) : Feature<RisingSignConfig, ZodiacSign> {
  override val key: String = "risingSign"

  override val defaultConfig: RisingSignConfig = RisingSignConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: RisingSignConfig): ZodiacSign {
    return when (config.impl) {
      RisingSignConfig.Impl.HouseCusp   -> houseCuspFeature.getModel(gmtJulDay, loc, config.houseCuspConfig)
      RisingSignConfig.Impl.TradChinese -> tradChineseRisingSignFeature.getModel(gmtJulDay, loc, config.tradChineseRisingSignConfig)
    }
  }
}
