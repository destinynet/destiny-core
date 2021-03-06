/**
 * Created by smallufo on 2018-06-23.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeSecDecoratorChinese
import destiny.core.calendar.TimeTools
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas
import mu.KotlinLogging
import java.time.format.DateTimeFormatter

class TimeLine(val model: IEightWordsContextModel,
               private val julDayResolver: JulDayResolver) : ColorCanvas(5, 70, ChineseStringTools.NULL_CHAR) {

  init {
    val centerSign: Pair<ZodiacSign, Double> =
      if (model.solarTermsTimePos.firstHalf) {
        // 時刻 在 前半節氣
        model.nextSolarSign
      } else {
        // 後半 節氣
        model.prevSolarSign
      }

    // 最左邊 節氣
    model.solarTermsTimePos.prevMajor.also { pair: Pair<SolarTerms, Double> ->
      val lmt = TimeTools.getLmtFromGmt(pair.second, model.location, julDayResolver)
      val title = timeDecorator.getOutputString(lmt)
      setText(pair.first.toString(), 1, 1, title = title)
      setText(monthDayFormatter.format(lmt.toLocalDate()), 2, 1, title = title)
      //setText(pair.first.branch.toString() , 1 , 5 , "white" , "teal" , null)
      setText(pair.first.branch.toString(), 1, 5, "white", "teal", null, null, false, null)
    }

    // 最右邊 節氣
    model.solarTermsTimePos.nextMajor.also { pair ->
      val lmt = TimeTools.getLmtFromGmt(pair.second, model.location, julDayResolver)
      val title = timeDecorator.getOutputString(lmt)
      setText(pair.first.toString(), 1, 63, title = title)
      setText(monthDayFormatter.format(lmt.toLocalDate()), 2, 63, title = title)
      setText(pair.first.branch.toString(), 1, 67, "white", "teal", null, null, false, null)
    }

    // 中間 星座

    centerSign.also { sign ->

      val middle = model.solarTermsTimePos.prevMajor.first.next().toString() + "／" + sign.first.toString()
      val lmt = TimeTools.getLmtFromGmt(centerSign.second, model.location, julDayResolver)
      val title = timeDecorator.getOutputString(lmt)
      setText("$middle→", 1, 29, title = title)
      setText(monthDayFormatter.format(lmt.toLocalDate()), 2, 33, title = title)
    }

    // 中間 節氣(中氣)
    setText("┼───────────────┼───────────────┼→", 3, 1)

    // 左邊 15 blocks , 右邊 15 blocks , 中間 1 block , 共 31 blocks , 但要扣除生日 (1 block)
    // 故，全部 30 blocks


    // 到 左邊 節氣 的天數

    val toLeftDays = (model.gmtJulDay - model.solarTermsTimePos.prevMajor.second)
    val toRightDays = (model.solarTermsTimePos.nextMajor.second - model.gmtJulDay)

    val leftBlocks = ((toLeftDays / (toLeftDays + toRightDays)) * 30).toInt().let {
      when {
        it <= 2 -> 2
        it >= 28 -> 28
        else -> it
      }
    }
    val rightBlocks = 30 - leftBlocks
    logger.debug("left / right = {} / {}", leftBlocks, rightBlocks)
    setText("日→", 2, 3 + leftBlocks * 2, "red")
    val starIndex = 3 + leftBlocks * 2
    setText("★", 3, starIndex, "red")

    val left = toLeftDays.toInt().let { leftDays ->
      (ChineseStringTools.toBiggerDigits(leftDays) + "日").let {
        if (leftDays > 3) {
          "←$it"
        } else
          it
      }
    }
    val right = toRightDays.toInt().let { rightDays ->
      ChineseStringTools.toBiggerDigits(rightDays) + "日".let {
        if (rightDays > 1) {
          "$it→"
        } else
          it
      }
    }
    val (leftDaysString, rightDaysString) = left to right

    setText(leftDaysString, 4, starIndex - leftDaysString.length * 2, "gray")
    setText(rightDaysString, 4, starIndex + 2, "gray")

  } // init

  companion object {
    private val logger = KotlinLogging.logger { }
    private val monthDayFormatter = DateTimeFormatter.ofPattern("MMdd")
    private val timeDecorator = TimeSecDecoratorChinese()
  }

}
