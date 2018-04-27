/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

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
  val solarTermsImpl: ISolarTerms) : IEightWordsContext, IEightWordsFactory by eightWordsImpl, Serializable {

  private data class CacheKey(val lmt: ChronoLocalDateTime<*> , val location: ILocation , val place: String?)

  @Transient
  private val cacheThreadLocal = ThreadLocal<Pair<CacheKey , IEightWordsContextModel>>()

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

      val sunBranch = getBranchOf(Planet.SUN, lmt, location, starPositionImpl)
      val moonBranch = getBranchOf(Planet.MOON, lmt, location, starPositionImpl)

      return EightWordsContextModel(eightWords, lmt, location, place, chineseDate,
                                    prevMajorSolarTerms, nextMajorSolarTerms, risingSign,
                                    sunBranch, moonBranch)
    }

    val pair: Pair<CacheKey, IEightWordsContextModel>? = cacheThreadLocal.get()
    return if (pair == null) {
      val model = innerGetModel()
      cacheThreadLocal.set(key to model)
      model
    } else {
      return if (key == pair.first) {
        pair.second
      } else {
        val model = innerGetModel()
        cacheThreadLocal.set(key to model)
        model
      }
    }
  }

  /**
   * 計算命宮干支
   */
  private fun getRisingStemBranch(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  eightWords: EightWords,
                                  risingSignImpl: IRisingSign): StemBranch {
    // 命宮地支
    val risingBranch = risingSignImpl.getRisingSign(lmt, location, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    val risingStem = StemBranchUtils.getMonthStem(eightWords.year.stem, risingBranch)
    // 組合成干支
    return StemBranch[risingStem, risingBranch]
  }

  private fun getBranchOf(star: Star,
                          lmt: ChronoLocalDateTime<*>,
                          location: ILocation,
                          starPositionImpl: IStarPosition<*>): Branch {
    val pos = starPositionImpl.getPosition(star, lmt, location, Centric.GEO, Coordinate.ECLIPTIC)
    return ZodiacSign.getZodiacSign(pos.lng).branch
  }
}