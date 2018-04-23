/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords

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

/**
 * 2018-04 新版 [EightWordsContext]
 * 令其直接實作 [IEightWordsContext] , 直接以 LMT / Location 取得命盤
 */
class EightWordsContext(
  val eightWordsImpl: IEightWordsFactory,
  /** 取得陰陽曆轉換的實作  */
  val chineseDateImpl: IChineseDate,
  val yearMonthImpl: IYearMonth,
  val dayImpl: IDay,
  val hourImpl: IHour,
  val midnightImpl: IMidnight,
  val changeDayAfterZi: Boolean,
  val risingSignImpl: IRisingSign,
  val starPositionImpl: IStarPosition<*>,
  val solarTermsImpl: ISolarTerms) : IEightWordsContext, IEightWordsFactory by eightWordsImpl, Serializable {

  override fun getEightWordsContextModel(lmt: ChronoLocalDateTime<*>,
                                         location: ILocation,
                                         place: String?): IEightWordsContextModel {

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    // 現在的節氣
    val currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
    val eightWords = this.eightWordsImpl.getEightWords(lmt, location)
    val chineseDate = this.chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl,
                                                          midnightImpl, changeDayAfterZi)

    //val prevNextMajorSolarTerms = SolarTerms.getPrevNextMajorSolarTerms(currentSolarTerms)

    val (prevMajorSolarTerms , nextMajorSolarTerms) = solarTermsImpl.getMajorSolarTermsBetween(lmt , location)

    val risingSign = getRisingStemBranch(lmt, location, eightWords, risingSignImpl)

    val sunBranch = getBranchOf(Planet.SUN, lmt, location, starPositionImpl)
    val moonBranch = getBranchOf(Planet.MOON, lmt, location, starPositionImpl)

    return EightWordsContextModel(eightWords, lmt, location, place, chineseDate,
                                  prevMajorSolarTerms , nextMajorSolarTerms, risingSign,
                                  sunBranch, moonBranch)
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