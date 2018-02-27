/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 高度=2 , 寬度 = 4
 */
class ChartBlockCanvasSimple(cb: ChartBlock,
                             fore: String? = null,
                             bg: String? = null) :
  ColorCanvas(2, 4, ChineseStringTools.NULL_CHAR, fore, bg) {
  init {
    val symbolText = cb.symbol?.toString() ?: "中"
    setText(symbolText, 2, 1) // 左下 , 八卦名稱
    setText(ChineseStringTools.digitToChinese(cb.period), 2, 3) // 右下 , 大寫中文字

    setText(cb.mountain.toBiggerDigit(), 1, 1) // 左上角 , 山
    setText(cb.direction.toBiggerDigit(), 1, 3) // 右上角 , 向
  }
}

class ChartBlockCanvasFull(cb: ChartBlock, fore: String? = null,
                           bg: String? = null) :
  ColorCanvas(7, 16, ChineseStringTools.NULL_CHAR, fore, bg) {

  init {
    setText(cb.period.toChineseDigit(), 5, 11)
    val symbolString = cb.symbol?.toString() ?:"中"
    setText(symbolString, 5, 7)
    setText(cb.mountain.toString(), 3, 11)
    setText(cb.direction.toString(), 3, 12)
  }




}