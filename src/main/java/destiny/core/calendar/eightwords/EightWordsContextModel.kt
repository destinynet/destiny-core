/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.calendar.eightwords

import destiny.core.ITimeLoc
import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.SolarTermsTimePos
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.eightwords.EightWordsContextConfigBuilder.Companion.ewContext
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 目前這星體的位置，以及其「時辰」(「類似」地盤12宮)
 */
data class PositionWithBranch(
  private val pos: IPos,
  val hour: StemBranch
) : IPos by pos


/** 純粹八字（不含「人」的資料） */
interface IEightWordsContextModel : ITimeLoc {
  val eightWords: IEightWords

  /** 是否有日光節約  */
  val dst: Boolean

  val gmtMinuteOffset: Int

  /** 地點名稱  */
  val place: String?

  /** 農曆  */
  val chineseDate: ChineseDate

  /** 與前後節氣 （外加中氣、亦即星座） 的相對位置 */
  val solarTermsTimePos: SolarTermsTimePos


  /** 上一個(目前)星座 , 以及 GMT Jul Day */
  val prevSolarSign: Pair<ZodiacSign, GmtJulDay>

  /** 下一個星座 , 以及 GMT Jul Day */
  val nextSolarSign: Pair<ZodiacSign, GmtJulDay>

  /** 星體位置表 */
  val starPosMap: Map<Point, PositionWithBranch>

  /** 命宮 (上升星座) */
  val risingStemBranch: StemBranch

  /** 12地盤時辰 宮首 的黃道度數 */
  val houseMap: Map<Int, ZodiacDegree>

  /** 四至點 的 黃道度數 */
  val rsmiMap : Map<TransPoint, ZodiacDegree>

  /** 星體交角 */
  val aspectsDataSet: Set<AspectData>
}

/**
 * 純粹由「時間、地點」切入，不帶其他參數，取得八字盤 (不含「人」的資料）
 */
interface IEightWordsContext : IEightWordsStandardFactory {
  val chineseDateImpl: IChineseDate
  val risingSignImpl: IRisingSign

  @Deprecated("EightWordsContextFeature")
  fun getEightWordsContextModel(lmt: ChronoLocalDateTime<*>,
                                location: ILocation,
                                place: String?): IEightWordsContextModel

  val ewContextConfig: EightWordsContextConfig
    get() {
      return ewContext {
        eightWordsConfig = ewConfig
        risingSign {
          impl = when(this@IEightWordsContext.risingSignImpl) {
            is IHouseCusp -> RisingSignConfig.Impl.HouseCusp
            else          -> RisingSignConfig.Impl.TradChinese
          }
        }
      }
    }

}


/**
 * 一個八字命盤「額外」的計算結果 , 方便排盤輸出
 * Note : 仍然不包含「人」的資訊（性別、大運、歲數...等）
 */
data class EightWordsContextModel(
  override val eightWords: IEightWords,
  override val time: ChronoLocalDateTime<*>,
  override val location: ILocation,
  /** 地點名稱  */
  override val place: String?,
  /** 農曆  */
  override val chineseDate: ChineseDate,
  /** 與前後節氣 （外加中氣） 的相對位置 */
  override val solarTermsTimePos: SolarTermsTimePos,

  /** 上一個(目前)星座 , 以及 GMT Jul Day */
  override val prevSolarSign: Pair<ZodiacSign, GmtJulDay>,

  /** 下一個星座 , 以及 GMT Jul Day */
  override val nextSolarSign: Pair<ZodiacSign, GmtJulDay>,

  /** 星體位置表 */
  override val starPosMap: Map<Point, PositionWithBranch>,

  /** 命宮 (上升星座) */
  override val risingStemBranch: StemBranch,

  /** 12地盤時辰 宮首 的黃道度數 */
  override val houseMap: Map<Int, ZodiacDegree>,

  /** 四至點 的黃道度數 */
  override val rsmiMap : Map<TransPoint, ZodiacDegree>,

  /** 星體交角 */
  override val aspectsDataSet: Set<AspectData>
) : IEightWordsContextModel, Serializable {

  /** 是否有日光節約  */
  override val dst: Boolean

  override val gmtMinuteOffset: Int

  init {
    val (first, second) = TimeTools.getDstSecondOffset(time, location)
    this.dst = first
    this.gmtMinuteOffset = second / 60
  }
}
