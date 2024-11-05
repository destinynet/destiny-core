/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.*
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.CacheGrain
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache


@Serializable
data class EightWordsContextConfig(
  val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
  override val risingSignConfig: RisingSignConfig = RisingSignConfig(),
  override var zodiacSignConfig: ZodiacSignConfig = ZodiacSignConfig(),
  override var place : String? = null
  //override var houseConfig : HouseConfig = HouseConfig(HouseSystem.MERIDIAN, Coordinate.ECLIPTIC),
): IEightWordsContextConfig , IEightWordsConfig by eightWordsConfig , IRisingSignConfig by risingSignConfig

context(IEightWordsConfig, IRisingSignConfig)
@DestinyMarker
class EightWordsContextConfigBuilder : Builder<EightWordsContextConfig> {

  private var zodiacSignConfig: ZodiacSignConfig = ZodiacSignConfig()
  fun zodiacSign(block : ZodiacSignBuilder.() -> Unit = {}) {
    this.zodiacSignConfig = ZodiacSignBuilder.zodiacSign(block)
  }

  var place : String? = null

  override fun build(): EightWordsContextConfig {
    return EightWordsContextConfig(ewConfig, risingSignConfig, zodiacSignConfig, place)
  }

  companion object {
    context(IEightWordsConfig, IRisingSignConfig)
    fun ewContext(block : EightWordsContextConfigBuilder.() -> Unit = {}) : EightWordsContextConfig {
      return EightWordsContextConfigBuilder().apply(block).build()
    }
  }
}

class EightWordsContextFeature(private val eightWordsFeature: EightWordsFeature,
                               private val chineseDateFeature: ChineseDateFeature,
                               private val risingSignFeature: RisingSignFeature,
                               private val houseCuspFeature: IHouseCuspFeature,
                               private val zodiacSignFeature: ZodiacSignFeature,
                               private val starPositionImpl: IStarPosition<*>,
                               private val houseCuspImpl: IHouseCusp,
                               private val solarTermsImpl: ISolarTerms,
                               private val aspectCalculator: IAspectCalculator,
                               private val julDayResolver: JulDayResolver,
                               @Transient
                               private val ewContextFeatureCache : Cache<LmtCacheKey<*>, IEightWordsContextModel>) : AbstractCachedFeature<EightWordsContextConfig , IEightWordsContextModel>() {

  override val key: String = "ewContext"

  override val defaultConfig: EightWordsContextConfig = EightWordsContextConfig()

  @Suppress("UNCHECKED_CAST")
  override val lmtCache: Cache<LmtCacheKey<EightWordsContextConfig>, IEightWordsContextModel>
    get() = ewContextFeatureCache as Cache<LmtCacheKey<EightWordsContextConfig>, IEightWordsContextModel>

  override var lmtCacheGrain: CacheGrain? = CacheGrain.SECOND

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: EightWordsContextConfig): IEightWordsContextModel {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc , julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: EightWordsContextConfig): IEightWordsContextModel {


    val eightWords = eightWordsFeature.getModel(lmt, loc, config.eightWordsConfig)

    val chineseDate = chineseDateFeature.getModel(lmt, loc, config.eightWordsConfig.dayHourConfig)

    // 命宮地支
    val risingBranch = risingSignFeature.getModel(lmt, loc, config.risingSignConfig).branch
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    val risingStem = StemBranchUtils.getMonthStem(eightWords.year.stem, risingBranch)
    val risingSign = StemBranch[risingStem, risingBranch]

    // 日干
    val dayStem = eightWords.day.stem
    // 五星 + 南北交點
    val stars: List<Star> = listOf(*Planet.classicalArray, *LunarNode.trueArray)
    val starPosMap: Map<AstroPoint, PositionWithBranch> = stars.associateWith { p: AstroPoint ->
      val pos: IPos = starPositionImpl.getPosition(p as Star, lmt, loc, Centric.GEO, Coordinate.ECLIPTIC)

      val hourImpl = HourHouseImpl(houseCuspImpl, starPositionImpl, p)
      val hourBranch = hourImpl.getHour(lmt, loc)

      val hourStem = StemBranchUtils.getHourStem(dayStem, hourBranch)
      val hour = StemBranch[hourStem, hourBranch]
      PositionWithBranch(pos, hour)
    }


    val houseMap = houseCuspFeature.getModel(lmt, loc, config.houseConfig)

    // 四個至點的黃道度數
    val rsmiMap: Map<TransPoint, ZodiacDegree> =
      houseCuspFeature.getModel(lmt, loc, HouseConfig(HouseSystem.PLACIDUS, Coordinate.ECLIPTIC)).let { map ->
        mapOf(
          TransPoint.RISING to map[1]!!,
          TransPoint.NADIR to map[4]!!,
          TransPoint.SETTING to map[7]!!,
          TransPoint.MERIDIAN to map[10]!!,
        )
      }


    val (prevSolarSign, nextSolarSign) = zodiacSignFeature.getModel(lmt, loc)
    val solarTermsTimePos = solarTermsImpl.getSolarTermsPosition(lmt.toGmtJulDay(loc))
    val aspectDataSet = aspectCalculator.getAspectPatterns(starPosMap)

    return EightWordsContextModel(
      eightWords,
      lmt,
      loc,
      config.place,
      chineseDate,
      solarTermsTimePos,
      prevSolarSign,
      nextSolarSign,
      starPosMap,
      risingSign,
      houseMap,
      rsmiMap,
      aspectDataSet
    )



  }

  companion object {
    const val CACHE_EIGHTWORDS_CONTEXT_FEATURE = "ewContextFeatureCache"
  }
}
