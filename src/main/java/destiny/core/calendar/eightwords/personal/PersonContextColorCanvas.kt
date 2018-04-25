/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeSecDecoratorChinese
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.Direction
import destiny.core.calendar.eightwords.EightWordsColorCanvas
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.tools.AlignTools
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas
import org.apache.commons.lang3.StringUtils
import java.util.*

class PersonContextColorCanvas(private val personContext: IPersonContext,
                               /** 預先儲存已經計算好的結果  */
                               private val model: IPersonContextModel,
                               place: String,
                               /** 地支藏干的實作，內定採用標準設定  */
                               private val hiddenStemsImpl: IHiddenStems,
                               linkUrl: String,
                               private val direction: Direction) : ColorCanvas(32, 70, ChineseStringTools.NULL_CHAR) {

  var outputMode = ColorCanvas.OutputMode.HTML

  private val ewContextColorCanvas: EightWordsColorCanvas by lazy {
    val m: IEightWordsContextModel = personContext.getEightWordsContextModel(model.lmt, model.location, model.place)
    EightWordsColorCanvas(m, personContext, model.place ?: "", hiddenStemsImpl, linkUrl, direction)
  }


  private val timeDecorator = TimeSecDecoratorChinese()


  init {
    val metaDataColorCanvas = ewContextColorCanvas.metaDataColorCanvas
    add(metaDataColorCanvas, 1, 1) // 國曆 農曆 經度 緯度 短網址 等 MetaData

    setText("性別：", 1, 59)
    setText(model.gender.toString(), 1, 65) // '男' or '女'
    setText("性", 1, 67)

    setText("八字：", 10, 1)

    val eightWords = model.eightWords

    val reactionsUtil = ReactionUtil(this.hiddenStemsImpl)

    add(ewContextColorCanvas.eightWordsColorCanvas, 11, 9) // 純粹八字盤


    val 右方大運直 = ColorCanvas(9, 24, ChineseStringTools.NULL_CHAR)
    val 下方大運橫 = ColorCanvas(8, 70, ChineseStringTools.NULL_CHAR)

    val dataList = ArrayList(model.fortuneDataLarges)

    for (i in 1..dataList.size) {
      val fortuneData = dataList[i - 1]
      val startFortune = fortuneData.startFortuneAge
      val endFortune = fortuneData.endFortuneAge
      val stemBranch = fortuneData.stemBranch


      val startFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.startFortuneGmtJulDay, model.location, revJulDayFunc)
      val endFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.endFortuneGmtJulDay, model.location, revJulDayFunc)

      右方大運直.setText(AlignTools.alignRight(startFortune, 6), i, 1, "green", null,
                    "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt))
      右方大運直.setText("→", i, 9, "green")
      右方大運直.setText(AlignTools.alignRight(endFortune, 6), i, 13, "green", null,
                    "終運時刻：" + timeDecorator.getOutputString(endFortuneLmt))
      右方大運直.setText(stemBranch.toString(), i, 21, "green")
    }


    if (direction == Direction.R2L) {
      dataList.reverse()
    }


    val ageNoteImpls = personContext.ageNoteImpls

    for (i in 1..dataList.size) {
      val fortuneData = dataList[i - 1]

      val startFortune: String =
        ageNoteImpls.map { it -> it.getAgeNote(fortuneData.startFortuneGmtJulDay) }
          .filter { it !== null }
          .map { it -> it!! }
          .first()


      val stemBranch = fortuneData.stemBranch
      val startFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.startFortuneGmtJulDay, model.location, revJulDayFunc)



      下方大運橫.setText(StringUtils.center(startFortune, 6, ' '), 1, (i - 1) * 8 + 1, "green", null,
                    "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt))
      val reaction = reactionsUtil.getReaction(stemBranch.stem, eightWords.day.stem)
      下方大運橫.setText(reaction.toString().substring(0, 1), 2, (i - 1) * 8 + 3, "gray")
      下方大運橫.setText(reaction.toString().substring(1, 2), 3, (i - 1) * 8 + 3, "gray")
      下方大運橫.setText(stemBranch.stem.toString(), 4, (i - 1) * 8 + 3, "red")
      下方大運橫.setText(stemBranch.branch.toString(), 5, (i - 1) * 8 + 3, "red")
      下方大運橫.add(ewContextColorCanvas.地支藏干(stemBranch.branch, eightWords.day.stem), 6, (i - 1) * 8 + 1)
    }


    // 2017-10-25 起，右邊大運固定顯示虛歲
    setText("大運（虛歲）", 10, 55)
    add(右方大運直, 11, 47)
    add(下方大運橫, 22, 1)

    val 節氣 = ColorCanvas(2, width, ChineseStringTools.NULL_CHAR)
    val prevMajorSolarTerms: Pair<SolarTerms, Double> = model.prevMajorSolarTerms
    val nextMajorSolarTerms: Pair<SolarTerms, Double> = model.nextMajorSolarTerms

    val prevMajorSolarTermsTime = TimeTools.getLmtFromGmt(prevMajorSolarTerms.second , model.location , revJulDayFunc)

    節氣.setText(prevMajorSolarTerms.first.toString(), 1, 1)
    節氣.setText("：", 1, 5)
    節氣.setText(this.timeDecorator.getOutputString(prevMajorSolarTermsTime), 1, 7)

    val nextMajorSolarTermsTime = TimeTools.getLmtFromGmt(nextMajorSolarTerms.second , model.location , revJulDayFunc)
    節氣.setText(nextMajorSolarTerms.first.toString(), 2, 1)
    節氣.setText("：", 2, 5)
    節氣.setText(this.timeDecorator.getOutputString(nextMajorSolarTermsTime), 2, 7)

    add(節氣, 31, 1)
  }

  /** 取得八字命盤  */
  override fun toString(): String {
    return when (outputMode) {
      ColorCanvas.OutputMode.TEXT -> getTextOutput()
      ColorCanvas.OutputMode.HTML -> htmlOutput
    }
  }

  companion object {
    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
