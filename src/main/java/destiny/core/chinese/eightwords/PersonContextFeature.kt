/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsContextConfig
import destiny.core.calendar.eightwords.EightWordsContextConfigBuilder
import destiny.core.calendar.eightwords.EightWordsContextFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime

@Serializable
data class EightWordsPersonConfig(val eightwordsContextConfig: EightWordsContextConfig = EightWordsContextConfig(),
                                  val fortuneLargeConfig: FortuneLargeConfig = FortuneLargeConfig(),
                                  val fortuneSmallConfig: FortuneSmallConfig = FortuneSmallConfig()): java.io.Serializable {


}

@DestinyMarker
class PersonConfigBuilder : Builder<EightWordsPersonConfig> {

  var ewContextConfig: EightWordsContextConfig = EightWordsContextConfig()
  fun ewContextConfig(block: EightWordsContextConfigBuilder.() -> Unit = {}) {
    ewContextConfig = EightWordsContextConfigBuilder.ewContext(block)
  }

  var fortuneLargeConfig: FortuneLargeConfig = FortuneLargeConfig()

  var fortuneSmallConfig: FortuneSmallConfig = FortuneSmallConfig()

  override fun build(): EightWordsPersonConfig {
    return EightWordsPersonConfig(ewContextConfig, fortuneLargeConfig, fortuneSmallConfig)
  }

  companion object {
    fun ewPersonConfig(block: PersonConfigBuilder.() -> Unit = {}) : EightWordsPersonConfig {
      return PersonConfigBuilder().apply(block).build()
    }
  }
}

class PersonContextFeature(private val eightWordsContextFeature: EightWordsContextFeature,
                           private val intAgeImpl: IIntAge,
                           private val julDayResolver: JulDayResolver) : PersonFeature<EightWordsPersonConfig, IPersonContextModel> {
  override val key: String = "ewPerson"

  override val defaultConfig: EightWordsPersonConfig = EightWordsPersonConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: EightWordsPersonConfig): IPersonContextModel {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, gender, name, place, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: EightWordsPersonConfig): IPersonContextModel {
    val ewModel = eightWordsContextFeature.getModel(lmt, loc, config.eightwordsContextConfig)
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    // 1到120歲 , 每歲的開始、以及結束
    val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = intAgeImpl.getRangesMap(gender, gmtJulDay, loc, 1, 120)

    // 9 or 18 條大運
//    val n = when(config.fortuneLargeImpl) {
//      EightWordsPersonConfig.FortuneLarge.Trad -> 9
//      EightWordsPersonConfig.FortuneLarge.Span -> 18
//    }



    TODO("FortuneLargeFeature and FortuneSmallFeature")
  }
}
