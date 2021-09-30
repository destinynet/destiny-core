/**
 * Created by smallufo on 2021-09-30.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.SeasonalSymbolConfig
import destiny.core.iching.divine.DivineTraditionalConfig
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named

@Serializable
data class HoloConfig(val divineTraditionalConfig: DivineTraditionalConfig = DivineTraditionalConfig(),
                      val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
                      val seasonalSymbolConfig: SeasonalSymbolConfig = SeasonalSymbolConfig(SeasonalSymbolConfig.Impl.Holo()),
                      val threeKings: ThreeKingsAlgo? = ThreeKingsAlgo.HALF_YEAR,
                      val hexChange: HexChange = HexChange.DST): java.io.Serializable

@DestinyMarker
class HoloConfigBuilder : Builder<HoloConfig> {

  var divineTraditionalConfig: DivineTraditionalConfig = DivineTraditionalConfig()

  var eightWordsConfig: EightWordsConfig = EightWordsConfig()

  var seasonalSymbolConfig: SeasonalSymbolConfig = SeasonalSymbolConfig(SeasonalSymbolConfig.Impl.Holo())

  var threeKings: ThreeKingsAlgo? = ThreeKingsAlgo.HALF_YEAR

  var hexChange: HexChange = HexChange.DST

  override fun build(): HoloConfig {
    return HoloConfig(divineTraditionalConfig, eightWordsConfig, seasonalSymbolConfig, threeKings, hexChange)
  }

  companion object {
    fun holoConfig(block : HoloConfigBuilder.() -> Unit = {}) : HoloConfig {
      return HoloConfigBuilder().apply(block).build()
    }
  }
}

@Named
class HoloFeature(private val julDayResolver: JulDayResolver) : AbstractCachedPersonFeature<HoloConfig, IHolo>() {

  override val key: String = "holoFeature"

  override val defaultConfig: HoloConfig = HoloConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloConfig): IHolo {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloConfig): IHolo {
    TODO()
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
