/**
 * Created by smallufo on 2019-03-04.
 */
package destiny.core.chinese.holo

import destiny.core.chinese.IStemBranch
import destiny.core.chinese.IYinYang
import destiny.iching.Hexagram
import destiny.iching.IHexagram
import destiny.iching.Symbol
import java.io.Serializable




interface IHoloHexagram : IHexagram {

  enum class Scale {
    LIFE,    // 半輩子，意味： 先天卦 or 後天卦
    MAJOR,   // 大運 : 陽爻9年 , 陰爻6年
    YEAR,    // 流年
    MONTH,   // 流月
  }

  val scale: Scale

  /** 卦象 */
  val hexagram: Hexagram

  /** 元堂 在第幾爻 (1~6) */
  val yuanTang: Int

  /** start of GMT JulianDay */
  val start: Double

  /** end of GMT JulianDay */
  val end: Double

  override val lowerSymbol: Symbol
    get() = hexagram.lowerSymbol

  override val upperSymbol: Symbol
    get() = hexagram.upperSymbol
}

data class HoloHexagram(
  override val scale: IHoloHexagram.Scale,
  override val hexagram: Hexagram,
  override val yuanTang: Int,
  override val start: Double,
  override val end: Double) : IHoloHexagram, Serializable {

  override fun toString(): String {
    return "$hexagram 之 $yuanTang"
  }
}

