/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeSecDecoratorChinese
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.ContextColorCanvasWrapper
import destiny.core.calendar.eightwords.Direction
import destiny.tools.AlignTools
import destiny.tools.ColorCanvas.ColorCanvas
import org.apache.commons.lang3.StringUtils
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Function

class PersonContextColorCanvasWrapper(private val personContext: PersonContext,
                                      /** 預先儲存已經計算好的結果  */
                                      private val model: PersonContextModel, locationName: String,
                                      /** 地支藏干的實作，內定採用標準設定  */
                                      private val hiddenStemsImpl: HiddenStemsIF, linkUrl: String, private val direction: Direction) : ContextColorCanvasWrapper(personContext, model.lmt, model.location, locationName, hiddenStemsImpl, linkUrl, direction) {

  private var outputMode = OutputMode.HTML

  private val timeDecorator = TimeSecDecoratorChinese()

  enum class OutputMode {
    HTML, TEXT
  }

  fun setOutputMode(mode: OutputMode) {
    this.outputMode = mode
  }

  /** 取得八字命盤  */
  override fun toString(): String {
    val cc = ColorCanvas(32, 70, "　")

    val metaDataColorCanvas = metaDataColorCanvas

    cc.add(metaDataColorCanvas, 1, 1) // 國曆 農曆 經度 緯度 短網址 等 MetaData

    cc.setText("性別：", 1, 59)
    cc.setText(model.getGender().toString(), 1, 65) // '男' or '女'
    cc.setText("性", 1, 67)

    cc.setText("八字：", 10, 1)

    val eightWords = model.eightWords

    val reactionsUtil = ReactionsUtil(this.hiddenStemsImpl)

    cc.add(eightWordsColorCanvas, 11, 9) // 純粹八字盤


    val 右方大運直 = ColorCanvas(9, 24, "　")
    val 下方大運橫 = ColorCanvas(8, 70, "　", Optional.empty(), Optional.empty())

    val dataList = ArrayList(model.fortuneDatas)

    for (i in 1..dataList.size) {
      val fortuneData = dataList[i - 1]
      val startFortune = fortuneData.startFortuneAge
      val endFortune = fortuneData.endFortuneAge
      val stemBranch = fortuneData.stemBranch


      val startFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.startFortuneGmtJulDay, model.location, revJulDayFunc)
      val endFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.endFortuneGmtJulDay, model.location, revJulDayFunc)

      右方大運直.setText(AlignTools.alignRight(startFortune, 6), i, 1, "green", null, "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt))
      右方大運直.setText("→", i, 9, "green")
      右方大運直.setText(AlignTools.alignRight(endFortune, 6), i, 13, "green", null, "終運時刻：" + timeDecorator.getOutputString(endFortuneLmt))
      右方大運直.setText(stemBranch.toString(), i, 21, "green")
    }


    if (direction == Direction.R2L) {
      Collections.reverse(dataList)
    }


    val ageNoteImpls = personContext.ageNoteImpls

    for (i in 1..dataList.size) {
      val fortuneData = dataList[i - 1]


//      String startFortune = ageNoteImpls.stream().findFirst()
//        .map(impl -> impl.getAgeNote(fortuneData.getStartFortuneGmtJulDay()))
//        .filter(Optional::isPresent)
//        .map(Optional::get)
//        .orElse("");

      val startFortune: String =
        ageNoteImpls.map { it -> it.getAgeNote(fortuneData.startFortuneGmtJulDay) }
          .filter { it !== null }
          .map { it -> it!! }
          .first()


      val stemBranch = fortuneData.stemBranch
      val startFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.startFortuneGmtJulDay, model.location, revJulDayFunc)



      下方大運橫.setText(StringUtils.center(startFortune, 6, ' '), 1, (i - 1) * 8 + 1, "green", null, "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt))
      val reaction = reactionsUtil.getReaction(stemBranch.stem, eightWords.day.stem)
      下方大運橫.setText(reaction.toString().substring(0, 1), 2, (i - 1) * 8 + 3, "gray")
      下方大運橫.setText(reaction.toString().substring(1, 2), 3, (i - 1) * 8 + 3, "gray")
      下方大運橫.setText(stemBranch.stem.toString(), 4, (i - 1) * 8 + 3, "red")
      下方大運橫.setText(stemBranch.branch.toString(), 5, (i - 1) * 8 + 3, "red")
      下方大運橫.add(地支藏干(stemBranch.branch, eightWords.day.stem), 6, (i - 1) * 8 + 1)
    }


    // 2017-10-25 起，右邊大運固定顯示虛歲
    cc.setText("大運（虛歲）", 10, 55)
    cc.add(右方大運直, 11, 47)
    cc.add(下方大運橫, 22, 1)

    val 節氣 = ColorCanvas(2, cc.width, "　")
    val prevMajorSolarTerms = model.prevMajorSolarTerms
    val nextMajorSolarTerms = model.nextMajorSolarTerms

    val pair1 = TimeTools.splitSecond(personContext.getTargetMajorSolarTermsSeconds(-1))
    val prevMajorSolarTermsTime = model.lmt.plus(pair1.first.toLong(), ChronoUnit.SECONDS).plus(pair1.second.toLong(), ChronoUnit.NANOS)
    //LocalDateTime prevMajorSolarTermsTime =LocalDateTime.from(model.getLmt()).plusSeconds(pair1.v1()).plusNanos(pair1.v2());

    節氣.setText(prevMajorSolarTerms.toString(), 1, 1)
    節氣.setText("：", 1, 5)
    節氣.setText(this.timeDecorator.getOutputString(prevMajorSolarTermsTime), 1, 7)

    val pair2 = TimeTools.splitSecond(personContext.getTargetMajorSolarTermsSeconds(1))
    val nextMajorSolarTermsTime = model.lmt.plus(pair2.first.toLong(), ChronoUnit.SECONDS).plus(pair2.second.toLong(), ChronoUnit.NANOS)
    //LocalDateTime nextMajorSolarTermsTime =LocalDateTime.from(model.getLmt()).plusSeconds(pair2.v1()).plusNanos(pair2.v2());
    節氣.setText(nextMajorSolarTerms.toString(), 2, 1)
    節氣.setText("：", 2, 5)
    節氣.setText(this.timeDecorator.getOutputString(nextMajorSolarTermsTime), 2, 7)

    cc.add(節氣, 31, 1)

    return when (this.outputMode) {
      PersonContextColorCanvasWrapper.OutputMode.TEXT -> cc.toString()
      PersonContextColorCanvasWrapper.OutputMode.HTML -> cc.htmlOutput
    }
  } // toString()

  companion object {

    private val revJulDayFunc = Function<Double, ChronoLocalDateTime<*>> { JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
