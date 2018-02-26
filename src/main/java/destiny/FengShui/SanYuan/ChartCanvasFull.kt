/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.SymbolAcquired
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/* 56 x 25
   ┌────────┬────────┬────────┐
   │1    3    2    1│1    3    2    1│1    3    2    1│
   │                │                │                │
   │2    1    96   9│2    1    96   9│2    1    96   9│
   │                │                │                │
   │3    乾   一   8│3    乾   一   8│3    乾   一   8│
   │                │                │                │
   │4    5    6    7│4    5    6    7│4    5    6    7│
   ├────────┼────────┼────────┤
   │1    3    2    1│1    3    2    1│1    3    2    1│
   │                │                │                │
   │2    1    96   9│2    1    96   9│2    1    96   9│
   │                │                │                │
   │3    乾   一   8│3    乾   一   8│3    乾   一   8│
   │                │                │                │
   │4    5    6    7│4    5    6    7│4    5    6    7│
   ├────────┼────────┼────────┤
   │1    3    2    1│1    3    2    1│1    3    2    1│
   │                │                │                │
   │2    1    96   9│2    1    96   9│2    1    96   9│
   │                │                │                │
   │3    乾   一   8│3    乾   一   8│3    乾   一   8│
   │                │                │                │
   │4    5    6    7│4    5    6    7│4    5    6    7│
   └────────┴────────┴────────┘
   */
class ChartCanvasFull(chart: Chart,
                      fore: String? = null,
                      bg: String? = null) : ColorCanvas(29, 56, ChineseStringTools.NULL_CHAR) {
  init {
    setText("┌────────┬────────┬────────┐", 1, 1)
    setText("│                │                │                │", 2, 1)
    setText("│                │                │                │", 3, 1)
    setText("│                │                │                │", 4, 1)
    setText("│                │                │                │", 5, 1)
    setText("│                │                │                │", 6, 1)
    setText("│                │                │                │", 7, 1)
    setText("│                │                │                │", 8, 1)
    setText("├────────┼────────┼────────┤", 9, 1)
    setText("│                │                │                │", 10, 1)
    setText("│                │                │                │", 11, 1)
    setText("│                │                │                │", 12, 1)
    setText("│                │                │                │", 13, 1)
    setText("│                │                │                │", 14, 1)
    setText("│                │                │                │", 15, 1)
    setText("│                │                │                │", 16, 1)
    setText("├────────┼────────┼────────┤", 17, 1)
    setText("│                │                │                │", 18, 1)
    setText("│                │                │                │", 19, 1)
    setText("│                │                │                │", 20, 1)
    setText("│                │                │                │", 21, 1)
    setText("│                │                │                │", 22, 1)
    setText("│                │                │                │", 23, 1)
    setText("│                │                │                │", 24, 1)
    setText("├────────┴────────┴────────┤", 25, 1)
    setText("│　XX運，XX山XX向。觀點：背29朝33　　　　　　　　　　│", 26, 1)
    setText("│　　　　　　　　　　　　　　　　　　　　　　　　　　│", 27, 1)
    setText("│　　Destiny 命理網　熱情製作 http://destiny.to　　　│", 28, 1)
    setText("└──────────────────────────┘", 29, 1)
    setText(ChineseStringTools.digitToChinese(chart.period), 26, 5)
    setText(chart.mountain.toString(), 26, 11)
    setText(chart.mountain.opposite.toString(), 26, 15)
    setText(chart.view.toString(), 26, 29)
    setText(SymbolAcquired.getOppositeSymbol(chart.view).toString(), 26, 33)

    // 從底端開始，順時針，八個卦
    val coordinates = listOf(
      Pair(18, 21), // 底
      Pair(18, 3), // 左下
      Pair(10, 3), // 左
      Pair(2, 3), // 左上
      Pair(2, 21), // 上
      Pair(2, 39),  // 右上
      Pair(10, 39),  // 右
      Pair(18, 39)   // 右下
                            )

    val symbols = generateSequence(chart.view, { it -> SymbolAcquired.getClockwiseSymbol(it) }).take(8).toList()
    coordinates.zip(symbols).forEach { (pair, symbol) ->
      val blockCanvas = ChartBlockCanvasFull(chart.getChartBlock(symbol), fore, bg)
      add(blockCanvas, pair.first, pair.second)
    }

    // 中宮
    val centerBlockCanvas = ChartBlockCanvasFull(chart.getCenterBlock())
    add(centerBlockCanvas, 10, 21)
  }
}