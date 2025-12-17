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
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.eightwords.hazards.IHazardService
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.CacheGrain
import destiny.tools.DestinyMarker
import destiny.tools.serializers.LocaleSerializer
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import javax.cache.Cache

@Serializable
data class EightWordsPersonConfig(val eightwordsContextConfig: EightWordsContextConfig = EightWordsContextConfig(),
                                  override var fortuneLargeConfig: FortuneLargeConfig = FortuneLargeConfig(eightWordsConfig = eightwordsContextConfig.eightWordsConfig),
                                  override var fortuneSmallConfig: FortuneSmallConfig = FortuneSmallConfig(eightWordsConfig = eightwordsContextConfig.eightWordsConfig),
                                  override var ewContextScore: EwContextScore = EwContextScore.OctaDivide316,
                                  @Serializable(with = LocaleSerializer::class)
                                  val locale : Locale = Locale.getDefault()): IEightWordsPersonConfig , IEightWordsContextConfig by eightwordsContextConfig

@DestinyMarker
class PersonConfigBuilder(val iEwConfig : IEightWordsContextConfig) : Builder<EightWordsPersonConfig> {

  var fortuneLargeConfig: FortuneLargeConfig = FortuneLargeConfig()
  fun fortuneLarge(block: FortuneLargeConfigBuilder.() -> Unit = {}) {
    this.fortuneLargeConfig = with(iEwConfig) { FortuneLargeConfigBuilder.fortuneLarge(block) }
  }

  var fortuneSmallConfig: FortuneSmallConfig = FortuneSmallConfig()
  fun fortuneSmall(block: FortuneSmallConfigBuilder.() -> Unit = {}) {
    this.fortuneSmallConfig = with(iEwConfig) { FortuneSmallConfigBuilder.fortuneSmall(block) }
  }

  var ewContextScore: EwContextScore = EwContextScore.OctaDivide316

  var locale: Locale = Locale.getDefault()

  override fun build(): EightWordsPersonConfig {
    return EightWordsPersonConfig(iEwConfig.ewContextConfig, fortuneLargeConfig, fortuneSmallConfig, ewContextScore, locale)
  }

  companion object {
    context(config: IEightWordsContextConfig)
    fun ewPersonConfig(block: PersonConfigBuilder.() -> Unit = {}) : EightWordsPersonConfig {
      return PersonConfigBuilder(config).apply(block).build()
    }
  }
}

@Named
class PersonContextFeature(private val eightWordsContextFeature: EightWordsContextFeature,
                           @param:Named("intAge8wImpl")
                           private val intAgeImpl: IIntAge,
                           private val fortuneLargeFeature: IFortuneLargeFeature,
                           private val fortuneSmallFeature: FortuneSmallFeature,
                           private val julDayResolver: JulDayResolver,
                           private val hazardService: IHazardService,

                           private val ewContextScoreImplMap: Map<EwContextScore, IEwContextScore>,

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
    val gmtJulDay = lmt.toGmtJulDay(loc)
    // 1到120歲 , 每歲的開始、以及結束
    val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = intAgeImpl.getRangesMap(gender, gmtJulDay, loc, 1, 120)

    val larges: List<FortuneData> = fortuneLargeFeature.getPersonModel(lmt, loc, gender, name, place, config.fortuneLargeConfig)
    val smalls: List<FortuneData> = fortuneSmallFeature.getPersonModel(lmt, loc, gender, name, place, config.fortuneSmallConfig)

    val childHazards = hazardService.getChildHazardNotes(ewModel.eightWords, gender, config.locale)

    val score = ewContextScoreImplMap[config.ewContextScore]!!.getScore(ewModel)

    return PersonContextModel(ewModel, gender, name, larges, smalls, ageMap, childHazards, score)
  }

  companion object {
    const val CACHE_EIGHTWORDS_PERSON_FEATURE = "ewPersonFeatureCache"
  }
}
