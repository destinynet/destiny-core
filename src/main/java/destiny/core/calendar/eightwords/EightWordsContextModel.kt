/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.ILocation
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


interface IEightWordsContextModel {
  val eightWords: EightWords
  val lmt: ChronoLocalDateTime<*>
  val location: ILocation

  /** 是否有日光節約  */
  val dst: Boolean

  val gmtMinuteOffset: Int

  /** 地點名稱  */
  val place: String?
  /** 農曆  */
  val chineseDate: ChineseDate
  /** 上一個「節」  */
  val prevMajorSolarTerms: SolarTerms
  /** 下一個「節」  */
  val nextMajorSolarTerms: SolarTerms
  /** 命宮 (上升星座)  */
  val risingStemBranch: StemBranch
  /** 太陽位置  */
  val sunBranch: Branch
  /** 月亮位置  */
  val moonBranch: Branch
}

/**
 * 一個八字命盤「額外」的計算結果 , 方便排盤輸出
 * Note : 仍然不包含「人」的資訊（性別、大運、歲數...等）
 */
data class EightWordsContextModel(
  override val eightWords: EightWords,
  override val lmt: ChronoLocalDateTime<*>,
  override val location: ILocation,
  /** 地點名稱  */
  override val place: String?,
  /** 農曆  */
  override val chineseDate: ChineseDate,
  /** 上一個「節」  */
  override val prevMajorSolarTerms: SolarTerms,
  /** 下一個「節」  */
  override val nextMajorSolarTerms: SolarTerms,
  /** 命宮 (上升星座)  */
  override val risingStemBranch: StemBranch,
  /** 太陽位置  */
  override val sunBranch: Branch,
  /** 月亮位置  */
  override val moonBranch: Branch) : IEightWordsContextModel, Serializable {

  /** 是否有日光節約  */
  override val dst: Boolean

  override val gmtMinuteOffset: Int

  init {
    val (first, second) = TimeTools.getDstSecondOffset(lmt, location)
    this.dst = first
    this.gmtMinuteOffset = second / 60
  }
}
