/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.EightWordsContextModel
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import java.time.chrono.ChronoLocalDateTime

/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），
 * 方便未來 View 端直接存取。不用在 View 端計算。
 */
class PersonContextModel(
  val gender: Gender,

  eightWords: EightWords,

  lmt: ChronoLocalDateTime<*>,

  location: ILocation,

  place: String?,

  chineseDate: ChineseDate,

  /** 總共要輸出的大運  */
  val fortuneDatas: List<FortuneData>,

  risingStemBranch: StemBranch,

  sunBranch: Branch,

  moonBranch: Branch,

  prevNextMajorSolarTerms: Pair<SolarTerms, SolarTerms>,
  /** 歲數(可能是虛歲)，每歲的起訖時刻  */
  val ageMap: Map<Int, Pair<Double, Double>>) : EightWordsContextModel(eightWords, lmt, location, place, chineseDate, prevNextMajorSolarTerms.first, prevNextMajorSolarTerms.second, risingStemBranch, sunBranch, moonBranch)
