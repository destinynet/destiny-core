/** 2009/12/15 上午2:56:48 by smallufo  */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import destiny.tools.ChineseStringTools
import destiny.tools.ChineseStringTools.NULL_CHAR
import destiny.tools.canvas.ColorCanvas
import java.io.Serializable

/**
 * 將 Chart 包裝成 ColorCanvas
 */
class ChartCanvasFullWrapper(private val chart: Chart) : Serializable {

  private val chartCoordinate = Array<Array<ChartBlock?>>(3) { arrayOfNulls(3) }

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

  val colorCanvas: ColorCanvas
    get() {

      val chartCanvas = ColorCanvas(29, 56, "　").apply {
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
        setText(getChineseString(chart.period), 26, 5)
        setText(chart.mountain.toString(), 26, 11)
        setText(chart.mountain.opposite.toString(), 26, 15)
        setText(chart.view.toString(), 26, 29)
        setText(SymbolAcquired.getOppositeSymbol(chart.view).toString(), 26, 33)
      }


      val chartBlockCanvasArray =
        arrayOf(ColorCanvas(7, 16, NULL_CHAR), ColorCanvas(7, 16, NULL_CHAR), ColorCanvas(7, 16, NULL_CHAR), ColorCanvas(7, 16, NULL_CHAR),
                ColorCanvas(7, 16, NULL_CHAR), ColorCanvas(7, 16, NULL_CHAR), ColorCanvas(7, 16, NULL_CHAR), ColorCanvas(7, 16, NULL_CHAR),
                ColorCanvas(7, 16, NULL_CHAR))

      for (y in 0..2) {
        for (x in 0..2) {
          chartCoordinate[x][y]?.run {
            chartBlockCanvasArray[x * 3 + y].setText(getChineseString(period), 5, 11)
            chartBlockCanvasArray[x * 3 + y].setText(getSymbolString(symbol), 5, 7)
            chartBlockCanvasArray[x * 3 + y].setText(mountain.toString(), 3, 11)
            chartBlockCanvasArray[x * 3 + y].setText(direction.toString(), 3, 12)
          }

        }
      }

      chartCanvas.add(chartBlockCanvasArray[0], 2, 3)
      chartCanvas.add(chartBlockCanvasArray[1], 2, 21)
      chartCanvas.add(chartBlockCanvasArray[2], 2, 39)
      chartCanvas.add(chartBlockCanvasArray[3], 10, 3)
      chartCanvas.add(chartBlockCanvasArray[4], 10, 21)
      chartCanvas.add(chartBlockCanvasArray[5], 10, 39)
      chartCanvas.add(chartBlockCanvasArray[6], 18, 3)
      chartCanvas.add(chartBlockCanvasArray[7], 18, 21)
      chartCanvas.add(chartBlockCanvasArray[8], 18, 39)

      return chartCanvas
    }

  init {

    //chartCoordinate[0][0] = chart.getChartBlock(Symbol.巽); // |  9  |  5  |  7  |
    //chartCoordinate[0][1] = chart.getChartBlock(Symbol.離); // |[0,0]|[0,1]|[0,2]|
    //chartCoordinate[0][2] = chart.getChartBlock(Symbol.坤); // |-----|-----|-----|
    //chartCoordinate[1][0] = chart.getChartBlock(Symbol.震); // |  8  |  1  |  3  |

    //chartCoordinate[1][1] = chart.getChartBlock(null);       // |[1,0]|[1,1]|[1,2]|

    //chartCoordinate[1][2] = chart.getChartBlock(Symbol.兌); // |-----|-----|-----|
    //chartCoordinate[2][0] = chart.getChartBlock(Symbol.艮); // |  4  |  6  |  2  |
    //chartCoordinate[2][1] = chart.getChartBlock(Symbol.坎); // |[2,0]|[2,1]|[2,2]|
    //chartCoordinate[2][2] = chart.getChartBlock(Symbol.乾); // |-----------------|

    var tempSymbol = chart.view //底邊
    //從 [2,1] (底邊) 開始，順時鐘設定八個卦的方位
    chartCoordinate[2][1] = chart.getChartBlock(tempSymbol)    //底邊
    tempSymbol = SymbolAcquired.getClockwiseSymbol(tempSymbol)
    chartCoordinate[2][0] = chart.getChartBlock(tempSymbol)    //左下
    tempSymbol = SymbolAcquired.getClockwiseSymbol(tempSymbol)
    chartCoordinate[1][0] = chart.getChartBlock(tempSymbol)    //左邊
    tempSymbol = SymbolAcquired.getClockwiseSymbol(tempSymbol)
    chartCoordinate[0][0] = chart.getChartBlock(tempSymbol)    //左上
    tempSymbol = SymbolAcquired.getClockwiseSymbol(tempSymbol)
    chartCoordinate[0][1] = chart.getChartBlock(tempSymbol)    //上方
    tempSymbol = SymbolAcquired.getClockwiseSymbol(tempSymbol)
    chartCoordinate[0][2] = chart.getChartBlock(tempSymbol)    //右上
    tempSymbol = SymbolAcquired.getClockwiseSymbol(tempSymbol)
    chartCoordinate[1][2] = chart.getChartBlock(tempSymbol)    //右邊
    tempSymbol = SymbolAcquired.getClockwiseSymbol(tempSymbol)
    chartCoordinate[2][2] = chart.getChartBlock(tempSymbol)    //右下
  }

  /** 將數字 1~9 轉成中文  */
  private fun getChineseString(i: Int): String {
    return when (i) {
      1 -> "一"
      2 -> "二"
      3 -> "三"
      4 -> "四"
      5 -> "五"
      6 -> "六"
      7 -> "七"
      8 -> "八"
      9 -> "九"
      else -> ChineseStringTools.NULL_CHAR
    }
  }


  private fun getSymbolString(s: Symbol?): String {
    return s?.toString() ?: "中"
  }

}

