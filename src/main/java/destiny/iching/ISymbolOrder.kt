/**
 * Created by smallufo on 2020-07-04.
 */
package destiny.iching


interface ISymbolOrder {

  /**
   * 以順時針方向取得一卦
   */
  fun getClockwiseSymbol(s: Symbol): Symbol

  /**
   * 逆時針
   */
  fun getCounterClockwiseSymbol(s: Symbol): Symbol

  /**
   * 取得對沖之卦
   */
  fun getOppositeSymbol(s: Symbol): Symbol
}
