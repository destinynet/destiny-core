/**
 * Created by smallufo on 2019-11-25.
 */
package destiny.fengshui.sanyuan

import destiny.core.TriGrid
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import java.io.Serializable

enum class Scale {
  YEAR,
  MONTH,
  DAY,
  HOUR
}

data class NineStarModel(
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

  return TriGrid.values().map { triGrid ->
    val symbol: Symbol? = acquiredGridMap[triGrid]

    val map = Scale.values().mapNotNull { scale ->
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
