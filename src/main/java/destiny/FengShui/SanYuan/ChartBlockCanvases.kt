/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

class ChartBlockCanvasSimple(cb: ChartBlock,
                             fore: String? = null,
                             bg: String? = null) :
  ColorCanvas(2, 4, ChineseStringTools.NULL_CHAR, fore, bg) {
  init {
    val symbolText = cb.symbol?.toString() ?: "中"
    setText(symbolText, 2, 1) // 左下 , 八卦名稱
    setText(ChineseStringTools.digitToChinese(cb.period), 2, 3) // 右下 , 大寫中文字

    setText(" " + cb.mountain.toString(), 1, 1) // 左上角 , 山
    setText(cb.direction.toString() + " ", 1, 3) // 右上角 , 向
  }
}

class ChartBlockCanvasFull(cb: ChartBlock, fore: String? = null,
                           bg: String? = null) :
  ColorCanvas(7, 16, ChineseStringTools.NULL_CHAR, fore, bg) {

  init {
    setText(getChineseString(cb.period), 5, 11)
    val symbolString = cb.symbol?.toString() ?:"中"
    setText(symbolString, 5, 7)
    setText(cb.mountain.toString(), 3, 11)
    setText(cb.direction.toString(), 3, 12)
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
}