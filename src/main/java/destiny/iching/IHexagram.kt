/**
 * @author smallufo
 * Created on 2007/3/8 at 上午 3:44:36
 */
package destiny.iching

/**
 * 一個最基本的「卦」的資料，只有 取得 各爻陰陽 getLine(int index) / 取得六爻陰陽 getLines()  / 上卦 getUpperSymbol() / 下卦 getLowerSymbol() / 等介面
 */
interface IHexagram {

  /** 取得全部的陰陽  */
  val yinYangs: BooleanArray

  /** 取得上卦  */
  val upperSymbol: Symbol

  /** 取得下卦  */
  val lowerSymbol: Symbol

  /** 取得 010101 的表示法  */
  val binaryCode: String

  /** 取得第幾爻的陰陽 , 為了方便起見，index 為 1 至 6  */
  fun getLine(index: Int): Boolean

  /**
   * 第 line 爻動的話，變卦是什麼
   * @param lines [1~6]
   */
  fun getHexagram(vararg lines: Int): IHexagram
}
