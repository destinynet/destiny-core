/**
 * Created by smallufo on 2018-02-20.
 */
package destiny.iching.canvas

import destiny.core.chinese.SimpleBranch
import destiny.iching.divine.ISingleHexagramWithName
import destiny.iching.divine.Relative
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas


/**
 * 高度 9 x 寬度 32
 *
<pre>
　　　　　離火三世卦：火水未濟　

【伏　　神】　  【　本　　卦　】
　　　　　　  應兄弟▇▇▇  巳火
　　　　　　  　子孫▇　▇  未土
　　　　　　  　妻財▇▇▇  酉金
官鬼　己亥水  世兄弟▇　▇  午火
　　　　　　  　子孫▇▇▇  辰土
　　　　　　  　父母▇　▇  寅木
</pre>

或是
<pre>
　　　　　離火三世卦：火水未濟　
　　　　　　　　　　　　　　　　
【伏　　神】　　【　本　　卦　】
子孫　庚戌土  應兄弟▇▇▇己巳火
妻財　庚申金  　子孫▇　▇己未土
兄弟　庚午火  　妻財▇▇▇己酉金
官鬼　己亥水  世兄弟▇　▇戊午火
子孫　己丑土  　子孫▇▇▇戊辰土
父母　己卯木  　父母▇　▇戊寅木
</pre>
 */
class HexagramPlateCanvas(hex: ISingleHexagramWithName) : ColorCanvas(9, 32, ChineseStringTools.NULL_CHAR) {
  init {

    val 本卦描述 = ColorCanvas(1, 20).apply {
      setText(hex.symbol.toString() + hex.symbol.fiveElement.toString(), 1, 1)
      val name = when (hex.宮序) {
        1 -> "本宮卦"
        2 -> "一世卦"
        3 -> "二世卦"
        4 -> "三世卦"
        5 -> "四世卦"
        6 -> "五世卦"
        7 -> "遊魂卦"
        0 -> "歸魂卦"
        8 -> "歸魂卦"
        else -> throw IllegalArgumentException("本卦宮序 : ${hex.宮序}")
      }
      setText(name, 1, 5)
      setText("：", 1, 11)
      setText(hex.fullName, 1, 13)
    }
    add(本卦描述, 1, 11)

    val 伏神 = ColorCanvas(7, 12 , ChineseStringTools.NULL_CHAR).apply {
      setText("【伏　　神】　", 1, 1)
      (6 downTo 1)
        .asSequence()
        .filter { hex.伏神六親[it - 1] != null }
        .forEach { i ->
          hex.伏神六親[i - 1]?.also { rel: Relative ->
            setText(rel.toString(), 8 - i, 1)
            setText(ChineseStringTools.NULL_CHAR, 8 - i, 5)
            hex.伏神納甲[i - 1]!!.also {
              setText(it.stem.toString(), 8 - i, 7)
              setText(it.branch.toString(), 8 - i, 9, "blue", null, null, null, false, null)
              setText(it.branch.toString(), 8 - i, 9, "blue", null, null, null, false, null)
              setText(SimpleBranch.getFiveElement(it.branch).toString(), 8 - i, 11, "blue", null, null, null, false,
                null)
            }
          }
        }  // 有伏神
    }

    val 本卦世應 = ColorCanvas(6, 2).apply {
      for (i in 6 downTo 1) {
        when (i) {
          hex.世爻 -> setText("世", 7 - i, 1, "green", null, null, null, false, null)
          hex.應爻 -> setText("應", 7 - i, 1, "green", null, null, null, false, null)
          else -> setText(ChineseStringTools.NULL_CHAR, 7 - i, 1)
        }
      }
    }

    val 本卦六親 = ColorCanvas(6, 4).apply {
      for (i in 6 downTo 1) {
        setText(hex.六親[i - 1].toString(), 7 - i, 1)
      }
    }

    val 本卦陰陽 = HexagramCanvas(hex) // 六條槓


    val 本卦納甲 = ColorCanvas(6, 6).apply {
      for (i in 6 downTo 1) {
        hex.納甲[i - 1].also { sb ->
          setText(sb.stem.toString(), 7 - i, 1)
          setText(sb.branch.toString(), 7 - i, 3, "blue", null, null, null, false, null)
          setText(SimpleBranch.getFiveElement(sb.branch).toString(), 7 - i, 5, "blue", null, null, null, false, null)
        }
      }
    }

    add(伏神, 3, 1)
    add(本卦世應, 4, 15)
    add(本卦六親, 4, 17)
    setText("【　本　　卦　】", 3, 17)
    add(本卦陰陽, 4, 21)
    add(本卦納甲, 4, 27)
  }
}
