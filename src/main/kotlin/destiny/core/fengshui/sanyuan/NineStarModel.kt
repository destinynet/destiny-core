/**
 * Created by smallufo on 2019-11-25.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.Scale
import destiny.core.TriGrid
import destiny.core.chinese.IStemBranch
import destiny.core.iching.Symbol
import destiny.core.iching.SymbolAcquired
import java.io.Serializable

data class NineStarModel(
  val stemBranch: IStemBranch,
  val scale: Scale,
  val center: NineStar,
  val map: Map<Symbol, NineStar>) : Serializable

/**
 * 承上，只是以九宮格 表示，方便輸出
 */
data class TriGridModel(
  val triGrid: TriGrid,
  val symbol: Symbol?,
  val map: Map<Scale, NineStar>) : Serializable

fun List<NineStarModel>.toTriGridModel(view: Symbol): List<TriGridModel> {

  // 後天八卦，在以此 view 為底 的 九宮分佈
  val acquiredGridMap: Map<TriGrid, Symbol?> = TriGrid.getGridMap(view, SymbolAcquired)

  return TriGrid.entries.map { triGrid ->
    val symbol: Symbol? = acquiredGridMap[triGrid]

    val map = Scale.entries.mapNotNull { scale ->
      this.firstOrNull { it.scale == scale }?.let { nineStarModel: NineStarModel ->
        val nineStar: NineStar = symbol?.let { s ->
          nineStarModel.map[s]
        } ?: nineStarModel.center
        scale to nineStar
      }
    }.toMap()

    TriGridModel(triGrid, symbol, map)
  }.toList()
}
