/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.ZodiacSign
import destiny.core.calendar.ILocation
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/** 純粹八字（不含「人」的資料） */
interface IEightWordsContextModel {
  val eightWords: IEightWords

  val lmt: ChronoLocalDateTime<*>

  val location: ILocation

  /** 是否有日光節約  */
  val dst: Boolean

  val gmtMinuteOffset: Int

  /** 地點名稱  */
  val place: String?

  /** 農曆  */
  val chineseDate: ChineseDate

  /** 上一個「節」 , 以及 GMT Jul Day */
  val prevMajorSolarTerms: Pair<SolarTerms,Double>


  /** 下一個「節」 , 以及 GMT Jul Day */
  val nextMajorSolarTerms: Pair<SolarTerms,Double>

  /** 上一個(目前)星座 , 以及 GMT Jul Day */
  val prevSolarSign : Pair<ZodiacSign , Double>

  /** 下一個星座 , 以及 GMT Jul Day */
  val nextSolarSign : Pair<ZodiacSign , Double>

  /** 命宮 (上升星座)  */
  val risingStemBranch: StemBranch

  /** 太陽星座 */
  val sunSign: ZodiacSign

  /** 月亮星座 */
  val moonSign:ZodiacSign

  val gmtJulDay
    get() = TimeTools.getGmtJulDay(lmt , location)
}

/**
 * 純粹由「時間、地點」切入，不帶其他參數，取得八字盤 (不含「人」的資料）
 */
interface IEightWordsContext : IEightWordsFactory {
  /** 是否子初換日 */
  val changeDayAfterZi: Boolean
  val chineseDateImpl: IChineseDate
  val yearMonthImpl: IYearMonth
  val dayImpl: IDay
  val hourImpl: IHour
  val midnightImpl: IMidnight
  val risingSignImpl: IRisingSign

  fun getEightWordsContextModel(lmt: ChronoLocalDateTime<*>,
                                location: ILocation,
                                place: String?): IEightWordsContextModel

}


/**
 * 一個八字命盤「額外」的計算結果 , 方便排盤輸出
 * Note : 仍然不包含「人」的資訊（性別、大運、歲數...等）
 */
data class EightWordsContextModel(
  override val eightWords: IEightWords,
  override val lmt: ChronoLocalDateTime<*>,
  override val location: ILocation,
  /** 地點名稱  */
  override val place: String?,
  /** 農曆  */
  override val chineseDate: ChineseDate,
  /** 上一個「節」 , 以及 GMT Jul Day  */
  override val prevMajorSolarTerms: Pair<SolarTerms,Double>,
  /** 下一個「節」 , 以及 GMT Jul Day  */
  override val nextMajorSolarTerms: Pair<SolarTerms,Double>,
  /** 命宮 (上升星座)  */
  override val risingStemBranch: StemBranch,

  /** 上一個(目前)星座 , 以及 GMT Jul Day */
  override val prevSolarSign: Pair<ZodiacSign, Double>,

  /** 下一個星座 , 以及 GMT Jul Day */
  override val nextSolarSign: Pair<ZodiacSign, Double>,

  /** 月亮星座 */
  override val moonSign: ZodiacSign) : IEightWordsContextModel, Serializable {

  /** 太陽星座 */
  override val sunSign : ZodiacSign by lazy {
    prevSolarSign.first
  }

  /** 是否有日光節約  */
  override val dst: Boolean

  override val gmtMinuteOffset: Int

  init {
    val (first, second) = TimeTools.getDstSecondOffset(lmt, location)
    this.dst = first
    this.gmtMinuteOffset = second / 60
  }
}
