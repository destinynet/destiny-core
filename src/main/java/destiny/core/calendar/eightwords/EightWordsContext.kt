/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import destiny.astrology.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.chinese.IChineseDate
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
  override val yearMonthImpl: IYearMonth,
  override val dayImpl: IDay,
  override val hourImpl: IHour,
  override val midnightImpl: IMidnight,
  override val changeDayAfterZi: Boolean,
  override val risingSignImpl: IRisingSign,
  val starPositionImpl: IStarPosition<*>,
  val solarTermsImpl: ISolarTerms,
  val zodiacSignImpl: IZodiacSign) : IEightWordsContext, IEightWordsFactory by eightWordsImpl, Serializable {

  private data class CacheKey(val lmt: ChronoLocalDateTime<*> , val location: ILocation , val place: String?)

  private val cache : Cache<CacheKey , IEightWordsContextModel> = CacheBuilder.newBuilder()
    .maximumSize(100)
    .expireAfterAccess(10 , TimeUnit.SECONDS)
    .build()

  override fun getEightWordsContextModel(lmt: ChronoLocalDateTime<*>,
                                         location: ILocation,
                                         place: String?): IEightWordsContextModel {

    val key = CacheKey(lmt , location , place)

    fun innerGetModel() : IEightWordsContextModel {
      // 現在的節氣
      val eightWords = this.eightWordsImpl.getEightWords(lmt, location)
      val chineseDate = this.chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl,
                                                            midnightImpl, changeDayAfterZi)

      val (prevMajorSolarTerms, nextMajorSolarTerms) = solarTermsImpl.getMajorSolarTermsGmtBetween(lmt, location)

      val risingSign = getRisingStemBranch(lmt, location, eightWords, risingSignImpl)
      val sunSign = getSignOf(Planet.SUN, lmt, location, starPositionImpl)
      val moonSign = getSignOf(Planet.MOON, lmt, location, starPositionImpl)


      val (prevSolarSign , nextSolarSign) = zodiacSignImpl.getSignsBetween(Planet.SUN , lmt , location)

      return EightWordsContextModel(eightWords, lmt, location, place, chineseDate,
                                    prevMajorSolarTerms, nextMajorSolarTerms,
                                    risingSign ,
                                    prevSolarSign , nextSolarSign,
                                    moonSign
                                   )
    }

    return cache.get(key) { innerGetModel()}

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

  private fun getSignOf(star: Star,
                          lmt: ChronoLocalDateTime<*>,
                          location: ILocation,
                          starPositionImpl: IStarPosition<*>): ZodiacSign {
    val pos = starPositionImpl.getPosition(star, lmt, location, Centric.GEO, Coordinate.ECLIPTIC)
    return ZodiacSign.getZodiacSign(pos.lng)
  }


}