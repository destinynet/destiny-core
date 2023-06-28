/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.chinese.eightwords

import destiny.core.calendar.DateHourMinSecDecoratorTradChinese
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.Direction
import destiny.core.calendar.eightwords.EightWordsColorCanvas
import destiny.core.calendar.eightwords.TimeLine
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*

class PersonContextColorCanvas(private val config: PersonPresentConfig,
                               /** 預先儲存已經計算好的結果  */
                               private val model: IPersonPresentModel,
                               /** 地支藏干的實作，內定採用標準設定  */
                               private val hiddenStemsImpl: IHiddenStems,
                               private val linkUrl: String?,
                               private val direction: Direction,
                               julDayResolver: JulDayResolver,
                               /** 是否顯示納音 */
                               private val showNaYin: Boolean = false) : ColorCanvas(36, 70, ChineseStringTools.NULL_CHAR) {

  var outputMode = OutputMode.HTML

  private val ewContextColorCanvas: EightWordsColorCanvas by lazy {
    EightWordsColorCanvas(model, config.personContextConfig.eightwordsContextConfig, hiddenStemsImpl, linkUrl, direction, showNaYin)
  }

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
    val 下方大運橫 = ColorCanvas(10, 70, ChineseStringTools.NULL_CHAR)

    val dataList = model.fortuneDataLarges.toMutableList()
    logger.debug("dataList size = {}", dataList.size)

    // 右方大運直 (限定9柱)
    for (i in 1..9) {

      val fortuneData = dataList[i - 1]
      val selected = fortuneData.stemBranch == model.selectedFortuneLarge
      val startFortune = ChineseStringTools.toBiggerDigits(fortuneData.startFortuneAge).let {
        if (selected)
          "★$it"
        else
          it
      }
      val endFortune = ChineseStringTools.toBiggerDigits(fortuneData.endFortuneAge)
      val stemBranch = fortuneData.stemBranch

      val startFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.startFortuneGmtJulDay, model.location, julDayResolver)
      val endFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.endFortuneGmtJulDay, model.location, julDayResolver)

      val bgColor = if (selected) "CCC" else null

      val row = ColorCanvas(1, 24, ChineseStringTools.NULL_CHAR, null, bgColor)

      row.setText(ChineseStringTools.alignRight(startFortune, 6), 1, 1, foreColor = "green", backColor = null, title = "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt))
      row.setText("→", 1, 9, foreColor = "green", backColor = null, title = null)
      row.setText(ChineseStringTools.alignRight(endFortune, 6), 1, 13, foreColor = "green", backColor = null,
        title = "終運時刻：" + timeDecorator.getOutputString(endFortuneLmt))
      row.setText(stemBranch.toString(), 1, 21, "green")
      右方大運直.add(row, i, 1)
    }


    if (direction == Direction.R2L) {
      dataList.reverse()
    }

    // 下方大運橫
    if (dataList.size == 9) {
      // 完整九條大運 , 每條 height=10 , width = 6
      for (i in 1..dataList.size) {
        val fortuneData = dataList[i - 1]
        val selected = fortuneData.stemBranch == model.selectedFortuneLarge


        val stemBranch = fortuneData.stemBranch
        val startFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.startFortuneGmtJulDay, model.location, julDayResolver)


        val bgColor = if (selected) "DDD" else null
        val triColumn = ColorCanvas(10, 6, ChineseStringTools.NULL_CHAR, null, bgColor)

        // 年/月
        triColumn.run {
          // 西元年份
          val startFortuneWestYear = startFortuneLmt.get(ChronoField.YEAR_OF_ERA).toString()
          setText(
            StringUtils.center(startFortuneWestYear, 6, ' '), 1, 1, foreColor = "green", backColor = null,
            title = "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt)
          )

          // 加上月份
          val monthDay = startFortuneLmt.toLocalDate().let { value ->
            monthFormatter.format(value)
          }
          setText(
            StringUtils.center(monthDay, 6, ' '), 2, 1, foreColor = "green", backColor = null,
            title = "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt)
          )
        }


        if (showNaYin) {
          stemBranch.naYin?.also { naYin ->
            val name = naYin.name
            triColumn.setText(name[0].toString(), 4, 5, "plum")
            triColumn.setText(name[1].toString(), 5, 5, "plum")
            triColumn.setText(name[2].toString(), 6, 5, "plum")
          }
        }

        val reaction = reactionsUtil.getReaction(stemBranch.stem, eightWords.day.stem)
        triColumn.setText(reaction.toString().substring(0, 1), 3, 3, "gray")
        triColumn.setText(reaction.toString().substring(1, 2), 4, 3, "gray")
        triColumn.setText(stemBranch.stem.toString(), 5, 3, "red")
        triColumn.setText(stemBranch.branch.toString(), 6, 3, "red")
        triColumn.add(ewContextColorCanvas.getHiddenStemsCanvas(stemBranch.branch, eightWords.day.stem), 7, 1)

        下方大運橫.add(triColumn, 1, (i - 1) * 8 + 1)
      } // 1~9
    } else if (dataList.size == 18) {
      // 18條大運 , 分成上下兩條 , 每條 height=5 , width = 6
      for (i in 1..9) {
        for (j in 1..2) {
          val index = (2 * (i - 1) + j - 1).let {
            if (direction == Direction.L2R)
              it
            else {
              // 交換上下兩列
              if (j==1)
                it+1
              else
                it-1
            }
          }
          val fortuneData = dataList[index]

          val selected = fortuneData.stemBranch == model.selectedFortuneLarge

          val stemBranch = fortuneData.stemBranch
          val startFortuneLmt = TimeTools.getLmtFromGmt(fortuneData.startFortuneGmtJulDay, model.location, julDayResolver)
          // 顯示西元年份
          val startFortuneWestYear = startFortuneLmt.get(ChronoField.YEAR_OF_ERA).toString()

          val bgColor = if (selected) "DDD" else null
          val triColumnShort = ColorCanvas(5, 6, ChineseStringTools.NULL_CHAR, null, bgColor)

          val title = StringUtils.center(startFortuneWestYear, 6, ' ')
          triColumnShort.setText(title, 1, 1, foreColor = "green", backColor = null, title = "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt))

          val reaction = reactionsUtil.getReaction(stemBranch.stem, eightWords.day.stem)
          triColumnShort.setText(reaction.getAbbreviation(Locale.TAIWAN) , 2 , 3 , "gray")
          triColumnShort.setText(stemBranch.stem.toString() , 3 , 3 , "red")
          triColumnShort.setText(stemBranch.branch.toString() , 4 , 3 , "red")
          // 地支藏干
          val reactions = reactionsUtil.getReactions(stemBranch.branch, eightWords.day.stem)
          for (k in reactions.indices) {
            val eachReaction = reactions[k]
            triColumnShort.setText(ReactionUtil.getStem(eightWords.day.stem, eachReaction).toString(), 5, 5 - 2 * k, "gray") // 天干
          }
          //triColumnShort.setText("XX" , 5 , 3 , "gray")
          val (x, y) = ((j - 1) * 5 + 1) to ((i - 1) * 8 + 1)
          下方大運橫.add(triColumnShort, x, y)
        } // 1~2
      } // 1~ (18/2=9)

    } // 18條大運 , 分成上下兩條 , 每條 height=5 , width = 6


    // 2017-10-25 起，右邊大運固定顯示虛歲
    setText("大運（虛歲）", 10, 55)
    add(右方大運直, 11, 47)
    add(下方大運橫, 22, 1)

    add(TimeLine(model, julDayResolver) , 32 , 1)
  }

  /** 取得八字命盤  */
  override fun toString(): String {
    return when (outputMode) {
      OutputMode.TEXT -> getTextOutput()
      OutputMode.HTML -> htmlOutput
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
    private val monthFormatter = DateTimeFormatter.ofPattern("MM月")
    private val monthDayFormatter = DateTimeFormatter.ofPattern("MMdd")
    private val timeDecorator = DateHourMinSecDecoratorTradChinese
  }

}
