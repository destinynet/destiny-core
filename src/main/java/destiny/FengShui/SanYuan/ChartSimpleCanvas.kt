/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.SymbolAcquired
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas


class ChartSimpleCanvas(chart: Chart,
                        fore: String? = null,
                        bg: String? = null) : ColorCanvas(8, 16, ChineseStringTools.NULL_CHAR, fore, bg) {
  init {
    // 從底端開始，順時針，八個卦
    val coordinates = listOf(
      Pair(7, 7), // 底
      Pair(7, 1), // 左下
      Pair(4, 1), // 左
      Pair(1, 1), // 左上
      Pair(1, 7), // 上
      Pair(1, 13),  // 右上
      Pair(4, 13),  // 右
      Pair(7, 13)   // 右下
                            )

    val symbols = generateSequence(chart.view, { it -> SymbolAcquired.getClockwiseSymbol(it)}).take(8).toList()
    coordinates.zip(symbols).forEach { (pair , symbol) ->
      val blockCanvas = ChartBlockSimpleCanvas(chart.getChartBlock(symbol) , fore , bg)
      add(blockCanvas , pair.first , pair.second)
    }

    // 中宮
    val centerBlockCanvas = ChartBlockSimpleCanvas(chart.getCenterBlock())
    add(centerBlockCanvas , 4 , 7)
  }


}