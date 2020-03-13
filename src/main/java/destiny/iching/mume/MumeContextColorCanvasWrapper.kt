/**
 * @author smallufo
 * Created on 2007/2/11 at 上午 3:50:01
 */
package destiny.iching.mume

import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.FiveElement
import destiny.iching.IHexagram
import destiny.iching.Symbol
import destiny.iching.contentProviders.IHexNameFull
import destiny.tools.canvas.ColorCanvas
import java.io.Serializable
import java.util.*

/**
 * 將 梅花易的 Context (MumeContext) 包裝成彩色 ColorCanvas
 * @author smallufo
 */
class MumeContextColorCanvasWrapper(private val hexagramNameFull: IHexNameFull) : Serializable {

  lateinit var mumeContext: MumeContext
  var metaData: String? = null
  var dateInfo = "" //日期/時間/地點經緯度 等資料
  lateinit var eightWords: IEightWords


  override fun toString(): String {
    val c = ColorCanvas(12, 74, "　")
    val siteCanvas = ColorCanvas(2, 74, "　")

    siteCanvas.setText("Destiny 命理網", 1, 1, null, null, "https://destiny.to", "Destiny命理網", false, null)

    siteCanvas.setText("https://destiny.to  ", 1, 17)
    siteCanvas.setText("梅花易數線上排盤　$metaData", 2, 1)
    c.add(siteCanvas, 1, 1)
    val dateCanvas = ColorCanvas(1, 38, "　")
    dateCanvas.setText(dateInfo, 1, 1)
    c.add(dateCanvas, 3, 1)

    // 八字Canvas
    val ewCanvas = ColorCanvas(3, 8, "　")
    ewCanvas.setText("時日月年", 1, 1)
    ewCanvas.setText(eightWords.hour.stem.toString(), 2, 1) //時干
    ewCanvas.setText(eightWords.hour.branch.toString(), 3, 1) //時支
    ewCanvas.setText(eightWords.day.stem.toString(), 2, 3) //日干
    ewCanvas.setText(eightWords.day.branch.toString(), 3, 3) //日支
    ewCanvas.setText(eightWords.month.stem.toString(), 2, 5) //月干
    ewCanvas.setText(eightWords.month.branch.toString(), 3, 5) //月支
    ewCanvas.setText(eightWords.year.stem.toString(), 2, 7) //年干
    ewCanvas.setText(eightWords.year.branch.toString(), 3, 7) //年支
    c.add(ewCanvas, 1, 51)

    //純粹五個卦 , 不包含時間等其他資訊
    val mainCanvas = ColorCanvas(8, 74, "　")

    //體卦變卦
    val motivate = mumeContext.motivate //動爻
    if (motivate == 1 || motivate == 2 || motivate == 3
    ) {
      //體卦上卦
      mainCanvas.setText("體卦", 4, 1)
      //用卦下卦
      mainCanvas.setText("用卦", 7, 1)
    } else {
      //用卦上卦
      mainCanvas.setText("用卦", 4, 1)
      //體卦下卦
      mainCanvas.setText("體卦", 7, 1)
    }

    // 本卦canvas
    val srcCanvas = ColorCanvas(8, 12, "　").apply {
      setText("【本　卦】", 1, 1)
      add(getColorCanvas(mumeContext.hexagram), 2, 1)
    }
    mainCanvas.add(srcCanvas, 1, 5)

    //變爻
    if (mumeContext.hexagram.getBoolean(mumeContext.motivate))
      mainCanvas.setText("◎", 9 - mumeContext.motivate, 17)
    else
      mainCanvas.setText("〤", 9 - mumeContext.motivate, 17)

    // 變卦Canvas
    val dstCanvas = ColorCanvas(8, 12, "　").apply {
      setText("【變　卦】", 1, 1)
      add(getColorCanvas(mumeContext.targetHexagram), 2, 1)
    }
    mainCanvas.add(dstCanvas, 1, 21)

    // 互卦Canvas
    val middleSpanCanvas = ColorCanvas(8, 12, "　").apply {
      setText("【互　卦】", 1, 1)
      add(getColorCanvas(mumeContext.hexagram.middleSpanHexagram), 2, 1)
    }
    mainCanvas.add(middleSpanCanvas, 1, 35)

    // 錯卦Canvas
    val interlacedCanvas = ColorCanvas(8, 12, "　").apply {
      setText("【錯　卦】", 1, 1)
      add(getColorCanvas(mumeContext.hexagram.interlacedHexagram), 2, 1)
    }
    mainCanvas.add(interlacedCanvas, 1, 49)

    // 綜卦Canvas
    val reversedCanvas = ColorCanvas(8, 12, "　").apply {
      setText("【綜　卦】", 1, 1)
      add(getColorCanvas(mumeContext.hexagram.reversedHexagram), 2, 1)
    }

    mainCanvas.add(reversedCanvas, 1, 63)

    c.add(mainCanvas, 5, 1)
    return c.htmlOutput
  }

  private fun getColorCanvas(hexagram: IHexagram): ColorCanvas {
    val cc = ColorCanvas(7, 12, "　")

    val name = hexagramNameFull.getHexagram(hexagram, Locale.TRADITIONAL_CHINESE)
    //卦名
    if (name.length == 4)
      cc.setText(" $name ", 1, 1)
    else
      cc.setText(name, 1, 3)

    cc.add(getColorCanvas(hexagram.upperSymbol), 2, 1)
    cc.add(getColorCanvas(hexagram.lowerSymbol), 5, 1)
    return cc
  }

  private fun getColorCanvas(s: Symbol): ColorCanvas {
    //卦的顏色
    val color: String = when (s.fiveElement) {
      FiveElement.木 -> "GREEN"
      FiveElement.火 -> "RED"
      FiveElement.土 -> "CC6633"
      FiveElement.金 -> "999999"
      FiveElement.水 -> "BLACK"
    }
    val cc = ColorCanvas(3, 12, "　", color, null)
    for (i in 3 downTo 1) {
      if (s.getBooleanValue(i)
      ) cc.setText("▅▅▅▅▅", 4 - i, 1)
      else cc.setText("▅▅　▅▅", 4 - i, 1)
    }
    cc.setText(s.getName(), 2, 11)
    cc.setText(s.fiveElement.toString(), 3, 11)
    return cc
  }
}
