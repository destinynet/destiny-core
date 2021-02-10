package destiny.core

import destiny.core.iching.ISymbolOrder
import destiny.core.iching.Symbol

/** 3x3 Grid */
enum class TriGrid {
  B,   // 底
  LB,  // 左下
  L, // 左
  LU,  // 左上
  U, // 上
  RU, // 右上
  R, // 右
  RB, // 右下
  C; // 中間

  /** 順時針 */
  fun clockWise(): TriGrid? {
    return when (this) {
      C -> null
      B -> LB
      LB -> L
      L -> LU
      LU -> U
      U -> RU
      RU -> R
      R -> RB
      RB -> B
    }
  }

  companion object {

    fun getGridMap(view: Symbol , symbolOrder : ISymbolOrder): Map<TriGrid, Symbol?> {

      val grids: List<TriGrid> = generateSequence(B) { it.clockWise()!! }.take(8).toList()
      val chartBlocks: List<Symbol?> =
        generateSequence(view) { symbolOrder.getClockwiseSymbol(it) }.take(8)
          .toList()

      return grids.zip(chartBlocks).plusElement(Pair(C, null)).toMap()
    } // getGridMap
  }
}
