/**
 * Created by smallufo on 2021-06-13.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.eightwords.Direction
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.IStemBranch
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas


/**
 * 輸出 高3 , 寬4x6=24 格式 :
 * 　　　　男　　　　　　　
 * 　丁　　丙　　乙　　甲　
 * 　卯　　寅　　丑　　子　
 */
class PersonCoreCanvas3x24(gender: Gender, eightWords: IEightWords, direction: Direction) : ColorCanvas(3, 24, ChineseStringTools.NULL_CHAR) {

  init {
    val pillars = listOf(
      getOnePillar(eightWords.year),
      getOnePillar(eightWords.month),
      getOnePillar(eightWords.day),
      getOnePillar(eightWords.hour)
    ).let {
      if (direction === Direction.R2L) it.reversed()
      else it
    }

    val genderColor = if (gender == Gender.男) "blue" else "red"
    val genderY = if (direction === Direction.R2L) 9 else 15
    setText(gender.name, 1, genderY, genderColor)

    val pillarWidth = pillars.first().width

    pillars.forEachIndexed { index, colorCanvas ->
      add(colorCanvas, 2, index * pillarWidth + 1)
    }
  }

  private fun getOnePillar(stemBranch: IStemBranch): ColorCanvas {
    return ColorCanvas(2, 6, ChineseStringTools.NULL_CHAR, null, null).apply {
      setText(stemBranch.stem.toString(), 1, 3)
      setText(stemBranch.branch.toString(), 2, 3)
    }
  }
}
