/**
 * Created by smallufo on 2018-02-21.
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.IStemBranchOptional
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/** 垂直的干支表現 */
class StemBranchOptionalVerticalCanvas(sbo : IStemBranchOptional) : ColorCanvas(2, 2) {
  init {
    sbo.run {
      stem.also {
        if (it == null)
          setText(ChineseStringTools.NULL_CHAR , 1 , 1)
        else
          setText(it.toString(), 1, 1, "red", wrap = false)
      }

      branch.also {
        if (it == null)
          setText(ChineseStringTools.NULL_CHAR , 2 , 1)
        else
          setText(it.toString(), 2, 1, "red", wrap = false)
      }
    }
  }
}

/** 八字 : 高度 4 x 寬度 8 */
class EightWordsNullableCanvas(ewn: IEightWordsNullable, dir: Direction = Direction.R2L) :
  ColorCanvas(4, 8, ChineseStringTools.NULL_CHAR) {
  init {
    val yBar = ColorCanvas(4,2 , ChineseStringTools.NULL_CHAR)
    val mBar = ColorCanvas(4,2 , ChineseStringTools.NULL_CHAR)
    val dBar = ColorCanvas(4,2 , ChineseStringTools.NULL_CHAR)
    val hBar = ColorCanvas(4,2 , ChineseStringTools.NULL_CHAR)

    yBar.setText("年" , 1 , 1)
    mBar.setText("月" , 1 , 1)
    dBar.setText("日" , 1 , 1 , foreColor = "blue")
    hBar.setText("時" , 1 , 1)

    yBar.add(StemBranchOptionalVerticalCanvas(ewn.year) , 3 , 1)
    mBar.add(StemBranchOptionalVerticalCanvas(ewn.month) , 3 , 1)
    dBar.add(StemBranchOptionalVerticalCanvas(ewn.day) , 3 , 1)
    hBar.add(StemBranchOptionalVerticalCanvas(ewn.hour) , 3 , 1)

    when(dir) {
      Direction.R2L -> {
        add(hBar , 1 , 1)
        add(dBar , 1 , 3)
        add(mBar , 1 , 5)
        add(yBar , 1 , 7)
      }
      Direction.L2R -> {
        add(yBar , 1 , 1)
        add(mBar , 1 , 3)
        add(dBar , 1 , 5)
        add(hBar , 1 , 7)
      }
    }
  }
}