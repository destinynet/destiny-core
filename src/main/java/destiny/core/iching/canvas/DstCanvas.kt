/**
 * Created by smallufo on 2018-02-20.
 */

package destiny.core.iching.canvas

import destiny.core.chinese.SimpleBranch
import destiny.core.iching.divine.ICombinedWithMetaName
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 高度 9 x 寬度 24
 * <pre>
　坎水三世卦：水火既濟　
　　　　　　　　　　　　
　　　【　變　　卦　】　
戊子水　官鬼▇　▇應兄弟
戊戌土　子孫▇▇▇　官鬼
戊申金　妻財▇　▇　父母
己亥水　官鬼▇▇▇世兄弟
己丑土　子孫▇　▇　官鬼
己卯木　父母▇▇▇　子孫
</pre>
 */
class DstCanvas(val combined: ICombinedWithMetaName) : ColorCanvas(9, 24, ChineseStringTools.NULL_CHAR) {
  init {

    val 變卦描述 = ColorCanvas(1, 20, ChineseStringTools.NULL_CHAR).apply {
      setText(combined.dstModel.symbol.toString() + combined.dstModel.symbol.fiveElement.toString(), 1, 1)
      val name = when (combined.dstModel.宮序) {
        1 -> "變宮卦"
        2 -> "一世卦"
        3 -> "二世卦"
        4 -> "三世卦"
        5 -> "四世卦"
        6 -> "五世卦"
        7 -> "遊魂卦"
        0 -> "歸魂卦"
        8 -> "歸魂卦"
        else -> throw IllegalStateException("變卦宮序 : ${combined.dstModel.宮序}")
      }
      setText(name, 1, 5)
      setText("：", 1, 11)
      setText(combined.dstModel.fullName, 1, 13)
    }

    val 變卦納甲 = ColorCanvas(6, 6).apply {
      for (i in 6 downTo 1) {
        combined.dstModel.納甲[i - 1].also {
          setText(it.stem.toString(), 7 - i, 1)
          setText(it.branch.toString(), 7 - i, 3, "blue", null, null, null, false, null)
          setText(SimpleBranch.getFiveElement(it.branch).toString(), 7 - i, 5, "blue", null, null, null, false, null)
        }
      }
    }


    val 變卦對於本卦的六親 = ColorCanvas(6, 4).apply {
      for (i in 6 downTo 1) {
        setText(combined.變卦對於本卦的六親[i - 1].toString(), 7 - i, 1)
      }
    }

    val 變卦陰陽 = HexagramCanvas(combined.dst)

    val 變卦世應 = ColorCanvas(6, 2).apply {
      for (i in 6 downTo 1) {
        when (i) {
          combined.dstModel.世爻 -> setText("世", 7 - i, 1, "green", null, null, null, false, null)
          combined.dstModel.應爻 -> setText("應", 7 - i, 1, "green", null, null, null, false, null)
          else -> setText(ChineseStringTools.NULL_CHAR, 7 - i, 1)
        }
      }
    }

    val 變卦六親 = ColorCanvas(6, 4).apply {
      for (i in 6 downTo 1) {
        setText(combined.dstModel.六親[i - 1].toString(), 7 - i, 1)
      }
    }

    add(變卦描述, 1, 3)
    setText("　　　【　變　　卦　】", 3, 1)
    add(變卦納甲, 4, 1)
    add(變卦對於本卦的六親, 4, 9)
    add(變卦陰陽, 4, 13)
    add(變卦世應, 4, 19)
    add(變卦六親, 4, 21)
  }
}
