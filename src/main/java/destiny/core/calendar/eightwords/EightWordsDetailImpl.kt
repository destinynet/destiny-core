/**
 * Created by smallufo on 2017-10-27.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.*
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.Location
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

class EightWordsDetailImpl : IEightWordsDetail, Serializable {

  override fun getDetails(lmt: ChronoLocalDateTime<*>, location: Location, place: String, eightWordsImpl: IEightWords, yearMonthImpl: IYearMonth, chineseDateImpl: IChineseDate, dayImpl: IDay, hourImpl: IHour, midnightImpl: IMidnight, changeDayAfterZi: Boolean, risingSignImpl: IRisingSign, starPositionImpl: IStarPosition<*>, solarTermsImpl: ISolarTerms): EightWordsContextModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    // 現在的節氣
    val currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
    val eightWords = eightWordsImpl.getEightWords(lmt, location)
    val chineseDate = chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl, midnightImpl, changeDayAfterZi)

    val prevNextMajorSolarTerms = SolarTerms.getPrevNextMajorSolarTerms(currentSolarTerms)

    val risingSign = getRisingStemBranch(lmt, location, eightWords, risingSignImpl)

    val sunBranch = getBranchOf(Planet.SUN, lmt, location, starPositionImpl)
    val moonBranch = getBranchOf(Planet.MOON, lmt, location, starPositionImpl)

    return EightWordsContextModel(eightWords, lmt, location, place, chineseDate,
      prevNextMajorSolarTerms.first, prevNextMajorSolarTerms.second, risingSign,
      sunBranch, moonBranch)
  }

  /**
   * 計算命宮干支
   */
  private fun getRisingStemBranch(lmt: ChronoLocalDateTime<*>, location: Location, eightWords: EightWords, risingSignImpl: IRisingSign): StemBranch {
    // 命宮地支
    val risingBranch = risingSignImpl.getRisingSign(lmt, location, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    val risingStem = StemBranchUtils.getMonthStem(eightWords.year.stem, risingBranch)
    // 組合成干支
    return StemBranch.get(risingStem, risingBranch)
  }

  /**
   * @return 計算星體的地支位置
   */
  private fun getBranchOf(star: Star, lmt: ChronoLocalDateTime<*>, location: Location, starPositionImpl: IStarPosition<*>): Branch {
    val pos = starPositionImpl.getPosition(star, lmt, location, Centric.GEO, Coordinate.ECLIPTIC)
    return ZodiacSign.getZodiacSign(pos.lng).branch
  }
}
