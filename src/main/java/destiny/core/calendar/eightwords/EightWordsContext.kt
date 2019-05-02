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
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
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
  val zodiacSignImpl: IZodiacSign,
  val riseTransImpl : IRiseTrans) : IEightWordsContext, IEightWordsFactory by eightWordsImpl, Serializable {

  private data class CacheKey(val lmt: ChronoLocalDateTime<*>, val location: ILocation, val place: String?)

  /**
   * TODO : 2019-02-02 : use ehcache to replace guava cache . 在紫微排盤後，切換到其他排盤，在切回原紫微盤，無法做時辰切換 , 換用 ehcache 不知能否解決此 bug
   *
   * Caused by: com.google.common.util.concurrent.UncheckedExecutionException: com.google.common.util.concurrent.UncheckedExecutionException: java.lang.NullPointerException
   * at com.google.common.cache.LocalCache$Segment.get(LocalCache.java:2050)
   * at com.google.common.cache.LocalCache.get(LocalCache.java:3952)
   * at com.google.common.cache.LocalCache$LocalManualCache.get(LocalCache.java:4871)
   * at destiny.core.calendar.eightwords.EightWordsContext.getEightWordsContextModel(EightWordsContext.kt:73)
   */
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

      val risingSign = getRisingStemBranch(lmt, location, eightWords, risingSignImpl)


      // 日干
      val dayStem = eightWords.day.stem

      // 五星 + 南北交點
      val stars: List<Star> = listOf(*Planet.classicalArray , *LunarNode.meanArray)

      val starPosMap : Map<Star, PositionWithBranch> = stars.map { p ->
        val pos: IPos = starPositionImpl.getPosition(p , lmt , location , Centric.GEO , Coordinate.ECLIPTIC)
        val hourImpl = HourSolarTransImpl(riseTransImpl , p)
        val hourBranch: Branch = hourImpl.getHour(gmtJulDay , location)
        val hourStem = EightWordsImpl.getHourStem(dayStem , hourBranch)
        val hour = StemBranch[hourStem , hourBranch]
        p to PositionWithBranch(pos , hour)
      }.toMap()


      val (prevSolarSign, nextSolarSign) = zodiacSignImpl.getSignsBetween(Planet.SUN, lmt, location)

      val solarTermsTimePos = solarTermsImpl.getSolarTermsPosition(gmtJulDay)

      return EightWordsContextModel(eightWords, lmt, location, place, chineseDate,
        solarTermsTimePos, risingSign,
        prevSolarSign, nextSolarSign, starPosMap)
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


}