/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.*
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime


@Serializable
data class EightWordsContextConfig(
  val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
  val risingSignConfig: RisingSignConfig = RisingSignConfig(),
  val zodiacSignConfig: ZodiacSignConfig = ZodiacSignConfig(),
  val houseConfig : HouseConfig = HouseConfig(HouseSystem.MERIDIAN , Coordinate.ECLIPTIC),
  val place : String? = null
): java.io.Serializable

@DestinyMarker
class EightWordsContextConfigBuilder : Builder<EightWordsContextConfig> {
  private var eightWordsConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block : EightWordsConfigBuilder.() -> Unit = {}) {
    this.eightWordsConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  private var risingSignConfig: RisingSignConfig = RisingSignConfig()
  fun risingSign(block : RisingSignConfigBuilder.() -> Unit = {}) {
    this.risingSignConfig = RisingSignConfigBuilder.risingSign(block)
  }

  private var zodiacSignConfig: ZodiacSignConfig = ZodiacSignConfig()
  fun zodiacSign(block : ZodiacSignBuilder.() -> Unit = {}) {
    this.zodiacSignConfig = ZodiacSignBuilder.zodiacSign(block)
  }

  var houseConfig : HouseConfig = HouseConfig(HouseSystem.MERIDIAN , Coordinate.ECLIPTIC)
  fun house(block: HouseConfigBuilder.() -> Unit = {}) {
    this.houseConfig = HouseConfigBuilder.houseCusp(block)
  }

  var place : String? = null

  override fun build(): EightWordsContextConfig {
    return EightWordsContextConfig(eightWordsConfig, risingSignConfig, zodiacSignConfig, houseConfig, place)
  }

  companion object {
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
                               private val aspectsCalculator: IAspectsCalculator,
                               private val julDayResolver: JulDayResolver) : Feature<EightWordsContextConfig , IEightWordsContextModel> {

  override val key: String = "ewContext"

  override val defaultConfig: EightWordsContextConfig = EightWordsContextConfig()


  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: EightWordsContextConfig): IEightWordsContextModel {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc , julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: EightWordsContextConfig): IEightWordsContextModel {
    val eightWords = eightWordsFeature.getModel(lmt, loc, config.eightWordsConfig)

    val chineseDate = chineseDateFeature.getModel(lmt, loc , config.eightWordsConfig.dayHourConfig)

    // 命宮地支
    val risingBranch = risingSignFeature.getModel(lmt, loc, config.risingSignConfig).branch
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    val risingStem = StemBranchUtils.getMonthStem(eightWords.year.stem, risingBranch)
    val risingSign = StemBranch[risingStem , risingBranch]

    // 日干
    val dayStem = eightWords.day.stem
    // 五星 + 南北交點
    val stars: List<Star> = listOf(*Planet.classicalArray, *LunarNode.meanArray)
    val starPosMap: Map<Point, PositionWithBranch> = stars.associateWith { p: Point ->
      val pos: IPos = starPositionImpl.getPosition(p as Star, lmt, loc, Centric.GEO, Coordinate.ECLIPTIC)

      val hourImpl = HourHouseImpl(houseCuspImpl, starPositionImpl, p)
      val hourBranch = hourImpl.getHour(lmt, loc)

      val hourStem = StemBranchUtils.getHourStem(dayStem, hourBranch)
      val hour = StemBranch[hourStem, hourBranch]
      PositionWithBranch(pos, hour)
    }


    val houseMap = houseCuspFeature.getModel(lmt, loc, config.houseConfig)

    // 四個至點的黃道度數
    val rsmiMap: Map<TransPoint, ZodiacDegree> = houseCuspFeature.getModel(lmt, loc, HouseConfig(HouseSystem.PLACIDUS, Coordinate.ECLIPTIC)).let { map ->
      mapOf(
        TransPoint.RISING to map[1]!!,
        TransPoint.NADIR to map[4]!!,
        TransPoint.SETTING to map[7]!!,
        TransPoint.MERIDIAN to map[10]!!,
      )
    }


    val (prevSolarSign, nextSolarSign) = zodiacSignFeature.getModel(lmt, loc)
    val solarTermsTimePos = solarTermsImpl.getSolarTermsPosition(TimeTools.getGmtJulDay(lmt , loc))
    val aspectDataSet = aspectsCalculator.getAspectDataSet(starPosMap)

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
}
