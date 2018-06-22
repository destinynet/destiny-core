/**
 * Created by smallufo on 2018-06-23.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.ZodiacSign
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeSecDecoratorChinese
import destiny.core.calendar.TimeTools
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter

class TimeLine(val model: IEightWordsContextModel) : ColorCanvas(5, 70, ChineseStringTools.NULL_CHAR) {

  private val timeDecorator = TimeSecDecoratorChinese()
  private val monthDayFormatter = DateTimeFormatter.ofPattern("MMdd")

  init {
    val centerSign: Pair<ZodiacSign, Double> =
      if (model.nextSolarSign.second < model.nextMajorSolarTerms.second) {
        // 時刻 在 前半節氣
        model.nextSolarSign
      } else {
        // 後半 節氣
        model.prevSolarSign
      }

    // 最左邊 節氣
    setText(model.prevMajorSolarTerms.first.toString(), 1, 1)
    TimeTools.getLmtFromGmt(model.prevMajorSolarTerms.second, model.location,
                            revJulDayFunc).also { lmt ->
      setText(monthDayFormatter.format(lmt.toLocalDate()), 2, 1, false, null, null, null, null,
              timeDecorator.getOutputString(lmt))
    }
    // 最右邊 節氣
    setText(model.nextMajorSolarTerms.first.toString(), 1, 63)
    TimeTools.getLmtFromGmt(model.nextMajorSolarTerms.second, model.location,
                            revJulDayFunc).also { lmt ->
      setText(monthDayFormatter.format(lmt.toLocalDate()), 2, 63, false, null, null, null, null,
              timeDecorator.getOutputString(lmt))
    }

    // 中間 星座
    val middle = model.prevMajorSolarTerms.first.next().toString() + "／" + centerSign.first.toString()
    setText("$middle→", 1, 29)
    TimeTools.getLmtFromGmt(centerSign.second, model.location, revJulDayFunc).also { lmt ->
      setText(monthDayFormatter.format(lmt.toLocalDate()), 2, 33, false, null, null, null, null,
              timeDecorator.getOutputString(lmt))
    }
    // 中間 節氣(中氣)
    setText("┼───────────────┼───────────────┼→", 3, 1)

    // 左邊 15 blocks , 右邊 15 blocks , 中間 1 block , 共 31 blocks , 但要扣除生日 (1 block)
    // 故，全部 30 blocks


    // 到 左邊 節氣 的天數
    val toLeftDays = (model.gmtJulDay - model.prevMajorSolarTerms.second)
    val toRightDays = (model.nextMajorSolarTerms.second - model.gmtJulDay)

    val leftBlocks = ((toLeftDays / (toLeftDays + toRightDays)) * 30).toInt().let { it ->
      when {
        it <= 3 -> return@let 3
        it >= 27 -> return@let 27
        else -> it
      }
    }
    val rightBlocks = 30 - leftBlocks
    logger.debug("left / right = {} / {}", leftBlocks, rightBlocks)
    setText("①".repeat(leftBlocks), 4, 3)
    setText("日→", 2, 3 + leftBlocks * 2, "red")
    setText("★", 3, 3 + leftBlocks * 2, "red")
    setText("②".repeat(rightBlocks), 4, 5 + leftBlocks * 2)
  } // init

  companion object {
    private val logger = LoggerFactory.getLogger(TimeLine::class.java)!!
    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}