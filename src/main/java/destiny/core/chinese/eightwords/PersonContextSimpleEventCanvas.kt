/**
 * Created by smallufo on 2021-06-13.
 */
package destiny.core.chinese.eightwords

import destiny.core.calendar.eightwords.Direction
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranch
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 高度 10 , 寬度 40 , 標示大運，以及事件的流年
 * 範例：
 *
 * 　　　　男　　　　　　　　　　　　　　　
 * 　戊　　戊　　丙　　己　　　　　　　　　
 * 　午　　午　　子　　卯　　　　　　　　　
 * 大運：　　　　　　　　　　　　　　　　　
 * 90  80  70  60  49  39  29  19  9 　　
 * 丁  戊  己  庚  辛  壬  癸  甲  乙　　
 * 卯  辰  巳  午  未  申  酉  戌  亥　　
 * 流年：　　　　　　　　　　　　　　　　　
 * 丙  乙  甲  癸  壬  辛  庚  己  戊  丁
 * 午  巳  辰  卯  寅  丑  子  亥  戌  酉
 */
class PersonContextSimpleEventCanvas(personContext: IPersonContext, model: IPersonPresentModel, direction: Direction) :
  ColorCanvas(10, 40, ChineseStringTools.NULL_CHAR) {

  init {
    val coreCanvas = PersonCoreCanvas3x24(model.gender, model.eightWords, direction)
    add(coreCanvas, 1, 1)

    // 九條大運
    val fortuneDataList: List<FortuneData> = personContext.fortuneLargeImpl.getFortuneDataList(model.time, model.location, model.gender, 9)

    // 選定的大運
    val selectedFortuneLarge: IStemBranch = model.selectedFortuneLarge

    // 九條大運 canvas , 寬度 4x9=36
    val fortuneDataCanvases = fortuneDataList.map { getFortuneDataPillar(it, selectedFortuneLarge) }.let {
      if (direction === Direction.R2L)
        it.reversed()
      else
        it
    }

    setText("大運：", 4, 1, "gray")

    val pillarWidth = fortuneDataCanvases.first().width
    fortuneDataCanvases.forEachIndexed { index, colorCanvas ->
      add(colorCanvas, 5, (index) * pillarWidth + 1)
    }


    fortuneDataList.firstOrNull { it.stemBranch == selectedFortuneLarge }?.also { fortuneData ->
      setText("流年：", 8, 1, "gray")

      val presentYear: StemBranch = personContext.yearMonthImpl.getYear(model.viewGmt, model.location)


      // 十年流年
      val years = generateSequence(personContext.yearMonthImpl.getYear(fortuneData.startFortuneGmtJulDay, model.location)) {
        it.next(1)
      }.take(10).toList().let {
        if (direction === Direction.R2L)
          it.reversed()
        else
          it
      }

      years.map { yearSb ->
        ColorCanvas(2, 4).apply {
          val foreColor = if (yearSb != presentYear) null else "white"
          val bgColor = if (yearSb != presentYear) null else "RED"
          setText(yearSb.stem.name, 1, 3, foreColor, bgColor)
          setText(yearSb.branch.name, 2, 3, foreColor, bgColor)
        }
      }.forEachIndexed { index, colorCanvas ->
        add(colorCanvas, 9, index * 4 + 1)
      }
    }

  }

  private fun getFortuneDataPillar(fortuneData: FortuneData, selected: IStemBranch): ColorCanvas {
    return ColorCanvas(3, 4).apply {
      val foreColor = if (fortuneData.stemBranch != selected) null else "white"
      val bgColor = if (fortuneData.stemBranch != selected) null else "BLUE"
      setText(fortuneData.startFortuneAge.toString(), 1, 3, foreColor, bgColor)
      setText(fortuneData.stemBranch.stem.name, 2, 3, foreColor, bgColor)
      setText(fortuneData.stemBranch.branch.name, 3, 3, foreColor, bgColor)
    }
  }
}
