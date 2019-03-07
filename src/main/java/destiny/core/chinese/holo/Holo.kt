/**
 * Created by smallufo on 2019-02-27.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.*
import java.io.Serializable

interface INumberize {
  fun getNumber(stem: Stem): Int
  fun getNumber(branch: Branch): Set<Int>
}

class NumberizeImpl : INumberize, Serializable {
  override fun getNumber(stem: Stem): Int {
    return when (stem) {
      甲, 壬 -> 6
      乙, 癸 -> 2
      丙 -> 8
      丁 -> 7
      戊 -> 1
      己 -> 9
      庚 -> 3
      辛 -> 4
    }
  }

  override fun getNumber(branch: Branch): Set<Int> {
    return when (branch) {
      子, 亥 -> setOf(1, 6)
      寅, 卯 -> setOf(3, 8)
      巳, 午 -> setOf(2, 7)
      申, 酉 -> setOf(4, 9)
      辰, 戌, 丑, 未 -> setOf(5, 10)
    }
  }
}

interface IHolo {
  val ew: IEightWords

  val gender: Gender

  val yuan: Yuan

  /** 天數 */
  val heavenNumber: Int

  /** 地數 */
  val earthNumber: Int

  /** 先天卦 , with 元堂 (1~6) , 包含六爻中每年的流年資訊 , 6 or 9 年 */
  val hexagramCongenital: ILifeHoloHexagram

  /** 後天卦 , with 元堂 (1~6) , 包含六爻中每年的流年資訊 , 6 or 9 年 */
  val hexagramAcquired: ILifeHoloHexagram

  /** 天元氣 */
  val vigorousSymbolFromStem: Symbol

  /** 地元氣 */
  val vigorousSymbolFromBranch: Symbol

  /** 天元氣相反 */
  val vigorlessSymbolFromStem: Symbol

  /** 地元氣相反 */
  val vigorlessSymbolFromBranch: Symbol

  /**
   * 化工 : 得到季節力量者 , 這裡的季節，與中國的季節不同（遲了一個半月），反而與西洋的季節定義相同 : 二分二至 為 春夏秋冬的起點
   * 另外考慮了季月 艮坤 旺 18日
   */
  val seasonalSymbols: Set<Symbol>

  /** 化工反例 */
  val seasonlessSymbols: Set<Symbol>
}

data class Holo(
  override val ew: IEightWords,
  override val gender: Gender,
  override val yuan: Yuan,
  override val heavenNumber: Int,
  override val earthNumber: Int,
  override val hexagramCongenital: ILifeHoloHexagram,
  override val hexagramAcquired: ILifeHoloHexagram,
  override val vigorousSymbolFromStem: Symbol,
  override val vigorousSymbolFromBranch: Symbol,
  override val vigorlessSymbolFromStem: Symbol,
  override val vigorlessSymbolFromBranch: Symbol,
  override val seasonalSymbols: Set<Symbol>,
  override val seasonlessSymbols: Set<Symbol>) : IHolo




