/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.SymbolAcquired
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 帶有外框 的簡易 Chart Canvas
 * 高度 10 x 寬度 20
┌──┬──┬──┐
│７１│２６│９８│
│ 七 │ 三 │ 五 │
├──┼──┼──┤
│８９│６２│４４│
│ 六 │ 八 │ 一 │
├──┼──┼──┤
│３５│１７│５３│
│ 二 │ 四 │ 九 │
├────────┤
│xx運│yy山│zz向│
└────────┘
原文網址：https://read01.com/RMPAEE.html
 */
class ChartCanvasSimpleFramed(chart: Chart,
                              fore: String? = null,
                              bg: String? = null) : ColorCanvas(12, 20, ChineseStringTools.NULL_CHAR, fore, bg) {

  init {
    setText("┌──┬──┬──┐", 1, 1)
    setText("│７１│２６│９８│", 2, 1)
    setText("│ 七 │ 三 │ 五 │", 3, 1)
    setText("├──┼──┼──┤", 4, 1)
    setText("│８９│６２│４４│", 5, 1)
    setText("│ 六 │ 八 │ 一 │", 6, 1)
    setText("├──┼──┼──┤", 7, 1)
    setText("│３５│１７│５３│", 8, 1)
    setText("│ 二 │ 四 │ 九 │", 9, 1)
    setText("├──┴──┴──┤", 10, 1)
    setText("│03運　09山　15向│", 11, 1)
    setText("└────────┘", 12, 1)

    // 從底端開始，順時針，八個卦
    val coordinates = listOf(
      Pair(8, 9), // 底
      Pair(8, 3), // 左下
      Pair(5, 3), // 左
      Pair(2, 3), // 左上
      Pair(2, 9), // 上
      Pair(2, 15),  // 右上
      Pair(5, 15),  // 右
      Pair(8, 15)   // 右下
                            )

    val symbols = generateSequence(chart.view, { it -> SymbolAcquired.getClockwiseSymbol(it) }).take(8).toList()
    coordinates.zip(symbols).forEach { (pair, symbol) ->
      val blockCanvas = ChartBlockCanvasSimple(chart.getChartBlock(symbol), fore, bg)
      add(blockCanvas, pair.first, pair.second)
    }

    // 中宮
    val centerBlockCanvas = ChartBlockCanvasSimple(chart.getCenterBlock())
    add(centerBlockCanvas, 5, 9)

    setText(chart.period.toChineseDigit(), 11, 3)
    setText(chart.mountain.toString(), 11, 9)
    setText(chart.mountain.opposite.toString(), 11, 15)
  }
}
