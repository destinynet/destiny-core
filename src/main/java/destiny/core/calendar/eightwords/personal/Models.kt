/**
 * Created by smallufo on 2018-03-14.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.calendar.Location
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.EightWords
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IEightWordsModel {
  val eightWords: EightWords
  val lmt: ChronoLocalDateTime<*>
  val location: Location
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

  val gmtMinuteOffset: Int
    get() = TimeTools.getDstSecondOffset(lmt, location).second

  /** 是否有日光節約  */
  val dst: Boolean
    get() = TimeTools.getDstSecondOffset(lmt, location).first
}


data class EightWordsModel(override val eightWords: EightWords,
                           override val lmt: ChronoLocalDateTime<*>,
                           override val location: Location,
                           override val place: String?,
                           override val chineseDate: ChineseDate,
                           override val prevMajorSolarTerms: SolarTerms,
                           override val nextMajorSolarTerms: SolarTerms,
                           override val risingStemBranch: StemBranch) : IEightWordsModel, Serializable