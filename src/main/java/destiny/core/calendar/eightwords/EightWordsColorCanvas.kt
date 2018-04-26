/**
 * @author smallufo
 * Created on 2006/5/5 at 下午 05:11:12
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.*
import destiny.core.calendar.eightwords.personal.IHiddenStems
import destiny.core.calendar.eightwords.personal.ReactionUtil
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.tools.AlignTools
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas
import java.time.temporal.ChronoField
import java.time.temporal.ChronoField.*
import java.util.*
import kotlin.math.abs

/**
 * 純粹繪製『八字盤』，不包含『人』的因素（大運流年等）
 */
class EightWordsColorCanvas(

  private val model :IEightWordsContextModel,

  private val context : IEightWordsContext,

  /** 地點的名稱  */
  private val place: String,

  /** 地支藏干的實作，內定採用標準設定  */
  private val hiddenStemsImpl: IHiddenStems,
  /** 網址連結  */
  private val linkUrl: String?,
  /** 輸出方向，由左至右，還是由右至左  */
  private val direction: Direction) : ColorCanvas(20 , 52 , ChineseStringTools.NULL_CHAR ) {


  /** TODO : IoC Google Maps URL Builder  */
  private val urlBuilder = GoogleMapsUrlBuilder()

  var outputMode = ColorCanvas.OutputMode.HTML

  private val reactionUtil: ReactionUtil = ReactionUtil(hiddenStemsImpl)

  init {
    add(metaDataColorCanvas, 1, 1)
    add(eightWordsColorCanvas, 11, 1)
  }

  /**
   * 傳回八字命盤
   */
  override fun toString(): String {
    return when (this.outputMode) {
      ColorCanvas.OutputMode.TEXT -> getTextOutput()
      ColorCanvas.OutputMode.HTML -> htmlOutput
    }
  } //toString()

  /**
   * 取得 MetaData (國曆 農曆 經度 緯度 等資料)
   */
  val metaDataColorCanvas: ColorCanvas
    get() {
      val lmt = model.lmt
      val location = model.location
      val cc = ColorCanvas(9, 52, ChineseStringTools.NULL_CHAR)

      val 西元資訊 = ColorCanvas(1, 36, ChineseStringTools.NULL_CHAR)

      val timeData = with(StringBuilder()) {
        append("西元：")

        if (lmt.toLocalDate().get(ChronoField.YEAR) <= 0) append("前")
        else append(ChineseStringTools.NULL_CHAR)

        append(AlignTools.alignRight(lmt.get(YEAR_OF_ERA), 4, true))
        append("年")
        append(AlignTools.alignRight(lmt.get(MONTH_OF_YEAR), 2, true))
        append("月")
        append(AlignTools.alignRight(lmt.get(DAY_OF_MONTH), 2, true))
        append("日")
        append(AlignTools.alignRight(lmt.get(HOUR_OF_DAY), 2, true))
        append("時")
        append(AlignTools.alignRight(lmt.get(MINUTE_OF_HOUR), 2, true))
        append("分")
        append(AlignTools.alignRight(lmt.get(SECOND_OF_MINUTE), 4, true))
        append("秒")
      }

      西元資訊.setText(timeData.toString(), 1, 1)
      cc.add(西元資訊, 1, 1)


      val chineseDate = model.chineseDate
      cc.setText("農曆：(" + chineseDate.cycleOrZero + "循環)" + chineseDate, 2, 1)

      val url = urlBuilder.getUrl(location)

      val 地點名稱 = ColorCanvas(1, 44, ChineseStringTools.NULL_CHAR)
      地點名稱.setText("地點：", 1, 1)
      地點名稱.setText(place, 1, 7, false, null, null, null, url, place)
      val minuteOffset = location.minuteOffset?:TimeTools.getDstSecondOffset(lmt, location).second / 60

      minuteOffset.also { it ->
        val absValue = abs(it)
        if (it >= 0) {
          地點名稱.setText(" GMT時差：" + AlignTools.alignRight(it, 6, true) + "分鐘", 1, 25)
        } else {
          地點名稱.setText(" GMT時差：前" + AlignTools.alignRight(absValue, 4, true) + "分鐘", 1, 25)
        }
      }
      cc.add(地點名稱, 3, 1)


      val 經度 = ColorCanvas(1, 24, ChineseStringTools.NULL_CHAR)
      val lngText = LngDecorator.getOutputString(location.lng, Locale.TAIWAN)

      經度.setText(lngText, 1, 1, false, null, null, null, url, null)
      cc.add(經度, 4, 1)

      val 緯度 = ColorCanvas(1, 22, ChineseStringTools.NULL_CHAR)
      val latText = LatDecorator.getOutputString(location.lat, Locale.TAIWAN)

      緯度.setText("$latText ", 1, 1, false, null, null, null, url, null)
      cc.add(緯度, 4, 25)

      cc.setText("換日：" + if (context.changeDayAfterZi) "子初換日" else "子正換日", 5, 1)
      if (location.northSouth == NorthSouth.SOUTH) {
        val yearMonthImpl = context.yearMonthImpl
        if (yearMonthImpl is YearMonthSolarTermsStarPositionImpl) {
          cc.setText("南半球", 5, 35, "FF0000")
          cc.setText("月令：" + if (yearMonthImpl.isSouthernHemisphereOpposition()) "對沖" else "不對沖", 5, 41)
        }
      }

      cc.setText("日光節約：", 5, 19)
      val isDst = TimeTools.getDstSecondOffset(lmt, location).first
      val dstString = if (isDst) "有" else "無"
      cc.setText(dstString, 5, 29, if (isDst) "FF0000" else "" , "", null)

      cc.setText("子正是：" + context.midnightImpl.getTitle(Locale.TRADITIONAL_CHINESE), 6, 1, null, null,
                 context.midnightImpl.getDescription(Locale.TRADITIONAL_CHINESE))
      cc.setText("時辰劃分：" + context.hourImpl.getTitle(Locale.TRADITIONAL_CHINESE), 7, 1, null, null,
                 context.hourImpl.getDescription(Locale.TRADITIONAL_CHINESE))
      val risingLine = 8
      val 命宮 = model.risingStemBranch
      cc.setText("命宮：", risingLine, 1, null, null, "命宮")
      cc.setText(命宮.toString(), risingLine, 7, "FF0000", null, 命宮.toString())
      cc.setText("（" + context.risingSignImpl.getTitle(Locale.TAIWAN) + "）", risingLine, 11)

      val linkLine = 9
      if (linkUrl != null) {
        cc.setText("命盤連結  ", linkLine, 1)
        val showLinkUrl: String = if (linkUrl.length % 2 == 1) "$linkUrl "
        else linkUrl
        cc.setText(showLinkUrl, linkLine, 11, false, null, null, null, showLinkUrl, null)
      }
      return cc
    }

  /**
   * 取得八字彩色盤 (不含「人」的資料)
   * <pre>
   * 　時　　　　日　　　　月　　　　年　　　　　　　　　
   * 　柱　　　　柱　　　　柱　　　　柱　　　　　　　　　
   * 　：　　　　：　　　　：　　　　：　　　　　　　　　
   * 　比　　　　　　　　　食　　　　傷　　　　　　　　　
   * 　肩　　　　　　　　　神　　　　官　　　　　　　　　
   * 　癸　　　　癸　　　　乙　　　　甲　　　　　　　　　
   * 　亥　　　　卯　　　　亥　　　　午　　　　　　　　　
   * 　甲壬　　　　乙　　　甲壬　　　己丁　　　　　　　　
   * 　傷劫　　　　食　　　傷劫　　　七偏　　　　　　　　
   * 　官財　　　　神　　　官財　　　殺財　　　　　　　
  </pre> *
   */
  val eightWordsColorCanvas: ColorCanvas
    get() {
      val eightWords: EightWords = model.eightWords

      val pillars = listOf(getOnePillar(eightWords.year, "年", eightWords.day.stem),
                           getOnePillar(eightWords.month, "月", eightWords.day.stem),
                           getOnePillar(eightWords.day, "日", eightWords.day.stem),
                           getOnePillar(eightWords.hour, "時", eightWords.day.stem)).let {
        if (direction === Direction.R2L) it.reversed()
        else it
      }

      return ColorCanvas(10, 36, ChineseStringTools.NULL_CHAR, null, null).let {
        for (i in 1..4) {
          it.add(pillars[i - 1], 1, (i - 1) * 10 + 1)
        }
        it
      }
    }

  /** 取得「一柱」的 ColorCanvas , 10 x 6
   * <pre>
   * 　時　
   * 　柱　
   * 　：　
   * 　比　
   * 　肩　
   * 　癸　
   * 　亥　
   * 　甲壬
   * 　傷劫
   * 　官財
  </pre> *
   * @param stemBranch
   * @param pillarName "年" or "月" or "日" or "時"
   */
  private fun getOnePillar(stemBranch: StemBranch, pillarName: String, dayStem: Stem): ColorCanvas {
    val pillar = ColorCanvas(10, 6, ChineseStringTools.NULL_CHAR, null, null)
    pillar.setText(pillarName, 1, 3)
    pillar.setText("柱", 2, 3)
    pillar.setText("：", 3, 3)

    pillar.setText(stemBranch.stem.toString(), 6, 3, "red", null, stemBranch.stem.toString() + pillarName)
    pillar.setText(stemBranch.branch.toString(), 7, 3, "red", null, stemBranch.toString() + pillarName)

    if ("日" != pillarName) {
      val 干對日主 = reactionUtil.getReaction(stemBranch.stem, dayStem).toString()
      pillar.setText(干對日主.substring(0, 1), 4, 3, "gray")
      pillar.setText(干對日主.substring(1, 2), 5, 3, "gray")
    }
    pillar.add(地支藏干(stemBranch.branch, dayStem), 8, 1)
    return pillar
  }



  fun 地支藏干(地支: Branch, 天干: Stem): ColorCanvas {
    val reactionsUtil = ReactionUtil(this.hiddenStemsImpl)
    val resultCanvas = ColorCanvas(3, 6, ChineseStringTools.NULL_CHAR)
    val reactions = reactionsUtil.getReactions(地支, 天干)
    for (i in reactions.indices) {
      val eachReaction = reactions[i]
      resultCanvas.setText(ReactionUtil.getStem(天干, eachReaction).toString(), 1, 5 - 2 * i, "gray") // 天干
      resultCanvas.setText(eachReaction.toString().substring(0, 1), 2, 5 - 2 * i, "gray")
      resultCanvas.setText(eachReaction.toString().substring(1, 2), 3, 5 - 2 * i, "gray")

    }
    return resultCanvas
  }


}
