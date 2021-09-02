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
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache

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

class PersonContextFeature(private val eightWordsContextFeature: EightWordsContextFeature,
                           private val intAgeImpl: IIntAge,
                           private val fortuneLargeFeature: IFortuneLargeFeature,
                           private val fortuneSmallFeature: FortuneSmallFeature,
                           private val julDayResolver: JulDayResolver,
                           private val ewPersonFeatureCache: Cache<CacheKey, IPersonContextModel>) : PersonFeature<EightWordsPersonConfig, IPersonContextModel> {

  data class CacheKey(
    val lmt: ChronoLocalDateTime<*>,
    val loc: ILocation,
    val gender: Gender,
    val name: String?,
    val place: String?,
    val config: EightWordsPersonConfig
  ) : java.io.Serializable

  override val key: String = "ewPerson"

  override val defaultConfig: EightWordsPersonConfig = EightWordsPersonConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: EightWordsPersonConfig): IPersonContextModel {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, gender, name, place, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: EightWordsPersonConfig): IPersonContextModel {

    val cacheKey = CacheKey(lmt, loc, gender, name, place, config)
    logger.trace { "cacheKey hash = ${cacheKey.hashCode()}" }

    return ewPersonFeatureCache.get(cacheKey)?.also {
      logger.trace { "cache hit" }
    }?:run {
      logger.trace { "cache miss" }

      val ewModel: IEightWordsContextModel = eightWordsContextFeature.getModel(lmt, loc, config.eightwordsContextConfig.copy(place = place))
      val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
      // 1到120歲 , 每歲的開始、以及結束
      val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = intAgeImpl.getRangesMap(gender, gmtJulDay, loc, 1, 120)

      val larges: List<FortuneData> = fortuneLargeFeature.getModel(lmt, loc, gender, name, place, config.fortuneLargeConfig)
      val smalls: List<FortuneData> = fortuneSmallFeature.getModel(lmt, loc, gender, name, place, config.fortuneSmallConfig)

      PersonContextModel(ewModel, gender, name, larges, smalls, ageMap).also { model ->
        logger.trace { "Insert cacheKey${cacheKey.hashCode()} to model" }
        ewPersonFeatureCache.put(cacheKey, model)
      }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
    const val CACHE_EIGHTWORDS_PERSON_FEATURE = "ewPersonFeatureCache"
  }
}
