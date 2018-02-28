/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.FengShui.SanYuan

import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 高度 = 8 , 寬度 = 16
 *
７９　２５　９７
巽七　離三　坤五
　　　　　　　　
８８　６１　４３
震六　中八　兌一
　　　　　　　　
３４　１６　５２
艮二　坎四　乾九
 */
class ChartCanvasSimple(chart: IChartMntPresenter,
                        fore: String? = null,
                        bg: String? = null) : ColorCanvas(8, 16, ChineseStringTools.NULL_CHAR, fore, bg) {

  init {
    val coordinateMap = mapOf(
      Position.B to Pair(7, 7),
      Position.LB to Pair(7, 1),
      Position.L to Pair(4, 1),
      Position.LU to Pair(1, 1),
      Position.U to Pair(1, 7),
      Position.RU to Pair(1, 13),
      Position.R to Pair(4, 13),
      Position.RB to Pair(7, 13),
      Position.C to Pair(4, 7)
                             )

    coordinateMap.forEach { (pos, pair) ->
      val chartBlock = chart.posMap[pos]!!
      val blockCanvas = ChartBlockCanvasSimple(chartBlock, fore, bg)
      add(blockCanvas, pair.first, pair.second)
    }
  }
}