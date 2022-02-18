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
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.CacheGrain
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache
import javax.inject.Named

@Serializable
data class EightWordsPersonConfig(val eightwordsContextConfig: EightWordsContextConfig = EightWordsContextConfig(),
                                  val fortuneLargeConfig: FortuneLargeConfig = FortuneLargeConfig(),
                                  val fortuneSmallConfig: FortuneSmallConfig = FortuneSmallConfig()): java.io.Serializable

@DestinyMarker
class PersonConfigBuilder : Builder<EightWordsPersonConfig> {

  var ewContextConfig: EightWordsContextConfig = EightWordsContextConfig()
  fun ewContextConfig(block: EightWordsContextConfigBuilder.() -> Unit = {}) {
    ewContextConfig = EightWordsContextConfigBuilder.ewContext(block)
  }

  var fortuneLargeConfig: FortuneLargeConfig = FortuneLargeConfig()
  fun fortuneLarge(block: FortuneLargeConfigBuilder.() -> Unit = {}) {
    this.fortuneLargeConfig = FortuneLargeConfigBuilder.fortuneLarge(block)
  }

  var fortuneSmallConfig: FortuneSmallConfig = FortuneSmallConfig()
  fun fortuneSmall(block: FortuneSmallConfigBuilder.() -> Unit = {}) {
    this.fortuneSmallConfig = FortuneSmallConfigBuilder.fortuneSmall(block)
  }

  override fun build(): EightWordsPersonConfig {
    return EightWordsPersonConfig(ewContextConfig, fortuneLargeConfig, fortuneSmallConfig)
  }

  companion object {
    fun ewPersonConfig(block: PersonConfigBuilder.() -> Unit = {}) : EightWordsPersonConfig {
      return PersonConfigBuilder().apply(block).build()
    }
  }
}

@Named
class PersonContextFeature(private val eightWordsContextFeature: EightWordsContextFeature,
                           @Named("intAge8wImpl")
                           private val intAgeImpl: IIntAge,
                           private val fortuneLargeFeature: IFortuneLargeFeature,
                           private val fortuneSmallFeature: FortuneSmallFeature,
                           private val julDayResolver: JulDayResolver,
                           @Transient
                           private val ewPersonFeatureCache: Cache<LmtCacheKey<*>, IPersonContextModel>) : AbstractCachedPersonFeature<EightWordsPersonConfig, IPersonContextModel>() {

  override val key: String = "ewPerson"

  override val defaultConfig: EightWordsPersonConfig = EightWordsPersonConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: EightWordsPersonConfig): IPersonContextModel {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }

  override var lmtCacheGrain: CacheGrain? = CacheGrain.MINUTE

  @Suppress("UNCHECKED_CAST")
  override val lmtPersonCache: Cache<LmtCacheKey<EightWordsPersonConfig>, IPersonContextModel>
    get() = ewPersonFeatureCache as Cache<LmtCacheKey<EightWordsPersonConfig>, IPersonContextModel>

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: EightWordsPersonConfig): IPersonContextModel {

    val ewModel: IEightWordsContextModel = eightWordsContextFeature.getModel(lmt, loc, config.eightwordsContextConfig.copy(place = place))
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    // 1到120歲 , 每歲的開始、以及結束
    val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = intAgeImpl.getRangesMap(gender, gmtJulDay, loc, 1, 120)

    val larges: List<FortuneData> = fortuneLargeFeature.getPersonModel(lmt, loc, gender, name, place, config.fortuneLargeConfig)
    val smalls: List<FortuneData> = fortuneSmallFeature.getPersonModel(lmt, loc, gender, name, place, config.fortuneSmallConfig)

    return PersonContextModel(ewModel, gender, name, larges, smalls, ageMap)
  }

  companion object {
    const val CACHE_EIGHTWORDS_PERSON_FEATURE = "ewPersonFeatureCache"
  }
}
