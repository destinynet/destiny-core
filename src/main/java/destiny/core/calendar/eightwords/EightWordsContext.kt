/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import destiny.astrology.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import destiny.core.chinese.StemBranchUtils.getHourStem
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.concurrent.TimeUnit

/**
 * 2018-04 新版 [EightWordsContext]
 * 令其直接實作 [IEightWordsContext] , 直接以 LMT / Location 取得命盤
 */
class EightWordsContext(
  val eightWordsImpl: IEightWordsFactory,

  /** 陰陽曆轉換的實作  */
  override val chineseDateImpl: IChineseDate,
  /** 年月 */
  override val yearMonthImpl: IYearMonth,
  /** 日時 */
  override val dayHourImpl : IDayHour,

  override val risingSignImpl: IRisingSign,

  private val starPositionImpl: IStarPosition<*>,
  private val solarTermsImpl: ISolarTerms,
  private val houseCuspImpl : IHouseCusp,
  val zodiacSignImpl: IZodiacSign,
  val riseTransImpl : IRiseTrans) : IEightWordsContext, IEightWordsFactory by eightWordsImpl, Serializable {

  private data class CacheKey(val lmt: ChronoLocalDateTime<*>, val location: ILocation, val place: String?)

  private val cache: Cache<CacheKey, IEightWordsContextModel> = CacheBuilder.newBuilder()
    .maximumSize(100)
    .expireAfterAccess(10, TimeUnit.SECONDS)
    .build()

  override fun getEightWordsContextModel(lmt: ChronoLocalDateTime<*>,
                                         location: ILocation,
                                         place: String?): IEightWordsContextModel {

    val key = CacheKey(lmt, location, place)

    fun innerGetModel(): IEightWordsContextModel {
      val gmtJulDay = TimeTools.getGmtJulDay(lmt , location)
      // 現在的節氣
      val eightWords = this.eightWordsImpl.getEightWords(lmt, location)
      val chineseDate = this.chineseDateImpl.getChineseDate(lmt, location, dayHourImpl)

      // 命宮
      val risingSign = getRisingStemBranch(lmt, location, eightWords, risingSignImpl)


      // 日干
      val dayStem = eightWords.day.stem

      // 五星 + 南北交點
      val stars: List<Star> = listOf(*Planet.classicalArray , *LunarNode.meanArray)

      val starPosMap : Map<Point, PositionWithBranch> = stars.map { p ->
        val pos: IPos = starPositionImpl.getPosition(p , lmt , location , Centric.GEO , Coordinate.ECLIPTIC)

        val hourImpl = HourHouseImpl(houseCuspImpl , starPositionImpl , p)
        val hourBranch = hourImpl.getHour(gmtJulDay, location)

        val hourStem = getHourStem(dayStem , hourBranch)
        val hour = StemBranch[hourStem , hourBranch]
        p to PositionWithBranch(pos , hour)
      }.toMap()


      val houseMap: Map<Int, Double> = houseCuspImpl.getHouseCuspMap(gmtJulDay , location , HouseSystem.MERIDIAN , Coordinate.ECLIPTIC)

      // 四個至點的黃道度數
      val placidusMap = houseCuspImpl.getHouseCusps(gmtJulDay , location , HouseSystem.PLACIDUS , Coordinate.ECLIPTIC)
      val rsmiMap = mutableMapOf<TransPoint , Double>().apply {
        put(TransPoint.RISING , placidusMap[1])
        put(TransPoint.NADIR , placidusMap[4])
        put(TransPoint.SETTING, placidusMap[7])
        put(TransPoint.MERIDIAN , placidusMap[10])
      }

      val (prevSolarSign, nextSolarSign) = zodiacSignImpl.getSignsBetween(Planet.SUN, lmt, location)

      val solarTermsTimePos = solarTermsImpl.getSolarTermsPosition(gmtJulDay)

      val calculator = HoroscopeAspectsCalculator(HoroscopeAspectsCalculatorModern())
      val aspectDataSet = calculator.getAspectDataSet(starPosMap)

      return EightWordsContextModel(eightWords, lmt, location, place, chineseDate,
        solarTermsTimePos,
        prevSolarSign, nextSolarSign, starPosMap ,risingSign, houseMap , rsmiMap , aspectDataSet)
    }

    return cache.get(key) { innerGetModel() }

  }

  /**
   * 計算命宮干支
   */
  private fun getRisingStemBranch(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  eightWords: IEightWords,
                                  risingSignImpl: IRisingSign): StemBranch {
    // 命宮地支
    val risingBranch = risingSignImpl.getRisingSign(lmt, location, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    val risingStem = StemBranchUtils.getMonthStem(eightWords.year.stem, risingBranch)
    // 組合成干支
    return StemBranch[risingStem, risingBranch]
  }


  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EightWordsContext) return false

    if (chineseDateImpl != other.chineseDateImpl) return false
    if (yearMonthImpl != other.yearMonthImpl) return false
    if (dayHourImpl != other.dayHourImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = chineseDateImpl.hashCode()
    result = 31 * result + yearMonthImpl.hashCode()
    result = 31 * result + dayHourImpl.hashCode()

    return result
  }


  companion object {
    val logger = KotlinLogging.logger {  }
  }

}