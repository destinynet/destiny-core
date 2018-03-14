/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.Location
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch

import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 一個八字命盤「額外」的計算結果 , 方便排盤輸出
 * Note : 仍然不包含「人」的資訊（性別、大運、歲數...等）
 */
open class EightWordsContextModel(val eightWords: EightWords,
                                  val lmt: ChronoLocalDateTime<*>,
                                  val location: Location,
                                  /** 地點名稱  */
                                  val place: String?,
                                  /** 農曆  */
                                  val chineseDate: ChineseDate,
                                  /** 上一個「節」  */
                                  val prevMajorSolarTerms: SolarTerms,
                                  /** 下一個「節」  */
                                  val nextMajorSolarTerms: SolarTerms,
                                  /** 命宮 (上升星座)  */
                                  val risingStemBranch: StemBranch,
                                  /** 太陽位置  */
                                  val sunBranch: Branch,
                                  /** 月亮位置  */
                                  val moonBranch: Branch) : Serializable {

  val gmtMinuteOffset: Int

  /** 日光節約  */
  val dst: Boolean


  init {
    val (first, second) = TimeTools.getDstSecondOffset(lmt, location)
    this.dst = first
    this.gmtMinuteOffset = second / 60
  }
}
