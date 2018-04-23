package destiny.core.calendar.eightwords

import destiny.astrology.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算八字盤（不包含「人」的資訊）
 * 除了計算八字，另外新增輸出農曆以及命宮的方法
 */
open class EightWordsContext(open val lmt: ChronoLocalDateTime<*>,
                             open val location: ILocation,
                             open val place: String?,
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
                             val solarTermsImpl: ISolarTerms
                            ) : IEightWordsContext, Serializable {

  val eightWords: EightWords by lazy { eightWordsImpl.getEightWords(lmt, location) }

  open val model: IEightWordsContextModel
    get() {
      return getEightWordsContextModel(lmt, location, place)
    }

  override fun getEightWordsContextModel(lmt: ChronoLocalDateTime<*>,
                                         location: ILocation,
                                         place: String?): IEightWordsContextModel {

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    // 現在的節氣
    val currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
    val eightWords = this.eightWordsImpl.getEightWords(lmt, location)
    val chineseDate = this.chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl,
                                                          midnightImpl, changeDayAfterZi)

    val prevNextMajorSolarTerms = SolarTerms.getPrevNextMajorSolarTerms(currentSolarTerms)

    val risingSign = getRisingStemBranch(lmt, location, eightWords, risingSignImpl)

    val sunBranch = getBranchOf(Planet.SUN, lmt, location, starPositionImpl)
    val moonBranch = getBranchOf(Planet.MOON, lmt, location, starPositionImpl)

    return EightWordsContextModel(eightWords, lmt, location, place, chineseDate,
                                  prevNextMajorSolarTerms.first, prevNextMajorSolarTerms.second, risingSign,
                                  sunBranch, moonBranch)
  }

  /**
   * 節氣
   */
  val currentSolarTerms: SolarTerms
    get() {
      return solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
//      val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
//
//      return solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
      //      val sp = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC)
      //      return SolarTerms.getFromDegree(sp.lng)
    }

  protected val gmtJulDay: Double
    get() = TimeTools.getGmtJulDay(lmt, location)

  /**
   * 上一個「節」、下一個「節」
   * 立春 , 驚蟄 , 清明 ...
   */
  val prevNextMajorSolarTerms: Pair<SolarTerms, SolarTerms>
    get() {
      val currentSolarTerms = currentSolarTerms
      return SolarTerms.getPrevNextMajorSolarTerms(currentSolarTerms)
    }

  /** 取得農曆  */
  val chineseDate: ChineseDate
    get() = chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl, midnightImpl, changeDayAfterZi)

  /**
   * 計算命宮干支
   */
  val risingStemBranch: StemBranch
    get() {
      return getRisingStemBranch(lmt, location, eightWords, risingSignImpl)
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
