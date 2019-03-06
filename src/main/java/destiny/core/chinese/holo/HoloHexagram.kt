/**
 * Created by smallufo on 2019-03-04.
 */
package destiny.core.chinese.holo

import destiny.core.chinese.IStemBranch
import destiny.core.chinese.IYinYang
import destiny.iching.IHexagram
import destiny.iching.Symbol


interface IHoloLine : IYinYang {
  val yinYang: IYinYang
  val yuanTang: Boolean
  val yearly: List<IHoloYearlyHexagram>

  val startGmtJulDay: Double
    get() = yearly.minBy { it.start }!!.start

  val endGmtJulDay: Double
    get() = yearly.maxBy { it.end }!!.end
}

data class HoloLine(override val yinYang: IYinYang,
                    override val yuanTang: Boolean,
                    override val yearly: List<IHoloYearlyHexagram>) : IHoloLine, IYinYang by yinYang

interface IHoloHexagram : IHexagram {

  val lines : List<IHoloLine>

  override val lowerSymbol: Symbol
    get() = Symbol.getSymbol(lines[0].booleanValue, lines[1].booleanValue, lines[2].booleanValue)

  override val upperSymbol: Symbol
    get() = Symbol.getSymbol(lines[3].booleanValue, lines[4].booleanValue, lines[5].booleanValue)

  /**
   * @param index 1 <= index <= 6
   */
  override fun getLineYinYang(index: Int): IHoloLine {
    require(index in 1..6) {
      "index should between 1 (incl.) and 6 (incl.)"
    }
    return lines[index - 1]
  }

  /** 元堂 在第幾爻 (1~6) */
  val yuanTang: Int
    get() = lines.withIndex().first { it.value.yuanTang }.index + 1

  /** 元堂爻的資訊 */
  val yuanTangLine : IHoloLine
    get() = lines.first { it.yuanTang }
}

data class HoloHexagram(override val lines: List<HoloLine>) : IHoloHexagram {
  init {
    require(lines.size == 6) {
      "lines length should be exactly 6"
    }
    require(lines.filter { it.yuanTang }.size == 1) {
      "必須要有唯一一個 元堂 爻"
    }
  }
}

/** 流年卦 */
interface IHoloYearlyHexagram : IHexagram {

  /** 流年干支 */
  val stemBranch : IStemBranch

  /** 流年卦象 */
  val hexagram: IHexagram

  /** 元堂 第幾爻 (1~6) */
  val yuanTang : Int

  /** start of GMT JulianDay */
  val start: Double

  /** end of GMT JulianDay */
  val end: Double
}

/** 流年卦 */
data class HoloYearlyHexagram(
  override val stemBranch: IStemBranch,
  override val hexagram : IHexagram,
  override val yuanTang: Int,
  override val start: Double,
  override val end: Double) : IHoloYearlyHexagram , IHexagram by hexagram