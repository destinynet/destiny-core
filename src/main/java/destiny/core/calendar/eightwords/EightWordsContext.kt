package destiny.core.calendar.eightwords

import destiny.astrology.*
import destiny.core.calendar.Location
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.SolarTermsImpl
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 除了計算八字，另外新增輸出農曆以及命宮的方法
 */
open class EightWordsContext(val lmt: ChronoLocalDateTime<*>,
                             protected val location: Location,
                             protected val eightWordsImpl: IEightWords,
                             val yearMonthImpl: IYearMonth,
                             /** 取得陰陽曆轉換的實作  */
                             val chineseDateImpl: IChineseDate,
                             protected val dayImpl: IDay,
                             val hourImpl: IHour,
                             val midnightImpl: IMidnight,
                             val isChangeDayAfterZi: Boolean,
                             val risingSignImpl: IRisingSign,
                             protected val starPositionImpl: IStarPosition<*>
) : Serializable {

  val eightWords: EightWords = eightWordsImpl.getEightWords(lmt, location)

  open val model: EightWordsContextModel
    get() {
      val prevNextMajorSolarTerms = prevNextMajorSolarTerms

      val chineseDate = chineseDate

      val risingSign = risingStemBranch
      val sunBranch = getBranchOf(Planet.SUN, lmt, location)
      val moonBranch = getBranchOf(Planet.MOON, lmt, location)
      return EightWordsContextModel(eightWords, lmt, location, "LOCATION", chineseDate,
        prevNextMajorSolarTerms.first,
        prevNextMajorSolarTerms.second,
        risingSign,
        sunBranch, moonBranch)
    }

  /**
   * 節氣
   * TODO 演算法重複 [SolarTermsImpl.getSolarTermsFromGMT]
   */
  val currentSolarTerms: SolarTerms
    get() {
      val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
      val sp = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC)
      return SolarTerms.getFromDegree(sp.lng)
    }

  val gmtJulDay: Double
    get() = TimeTools.getGmtJulDay(lmt, location)

  /** 上一個「節」、下一個「節」
   */
  //立春 , 驚蟄 , 清明 ...
  val prevNextMajorSolarTerms: Pair<SolarTerms, SolarTerms>
    get() {
      val currentSolarTerms = currentSolarTerms
      val currentSolarTermsIndex = SolarTerms.getIndex(currentSolarTerms)
      val prevMajorSolarTerms: SolarTerms
      val nextMajorSolarTerms: SolarTerms
      if (currentSolarTermsIndex % 2 == 0) {
        prevMajorSolarTerms = currentSolarTerms
        nextMajorSolarTerms = currentSolarTerms.next().next()
      } else {
        prevMajorSolarTerms = currentSolarTerms.previous()
        nextMajorSolarTerms = currentSolarTerms.next()
      }
      return Pair(prevMajorSolarTerms, nextMajorSolarTerms)
    }

  /** 取得農曆  */
  val chineseDate: ChineseDate
    get() = chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl, midnightImpl, isChangeDayAfterZi)

  /**
   * 計算命宮干支
   */
  // 命宮地支
  // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
  // 組合成干支
  val risingStemBranch: StemBranch
    get() {
      val risingBranch = risingSignImpl.getRisingSign(lmt, location, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
      val risingStem = StemBranchUtils.getMonthStem(eightWords.yearStem, risingBranch)
      return StemBranch.get(risingStem, risingBranch)
    }

  private fun getBranchOf(star: Star, lmt: ChronoLocalDateTime<*>, location: Location): Branch {
    val pos = starPositionImpl.getPosition(star, lmt, location, Centric.GEO, Coordinate.ECLIPTIC)
    return ZodiacSign.getZodiacSign(pos.lng).branch
  }
}
