/**
 * Created by smallufo on 2019-03-04.
 */
package destiny.core.chinese.holo

import destiny.core.chinese.IYinYang
import destiny.iching.IHexagram
import destiny.iching.Symbol


interface IHoloLine : IYinYang {
  val yinYang: IYinYang
  val yuanTang: Boolean
  val startFortuneGmtJulDay: Double
  val endFortuneGmtDay: Double
}

data class HoloLine(override val yinYang: IYinYang,
                    override val yuanTang: Boolean,
                    override val startFortuneGmtJulDay: Double,
                    override val endFortuneGmtDay: Double) : IHoloLine, IYinYang by yinYang

interface IHoloHexagram : IHexagram {

  /**
   * @param index 1 <= index <= 6
   */
  override fun getLineYinYang(index: Int): IHoloLine

}

data class HoloHexagram(val lines : List<HoloLine>) : IHoloHexagram {
  init {
    require(lines.size == 6) {
      "lines length should be exactly 6"
    }
  }

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
    return lines[index-1]
  }
}