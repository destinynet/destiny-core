/**
 * Created by smallufo on 2021-04-02.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.getAbbreviation
import destiny.core.astrology.toString
import destiny.core.calendar.eightwords.Direction
import destiny.core.chinese.toString
import destiny.tools.ChineseStringTools
import destiny.tools.LocaleTools
import destiny.tools.canvas.ColorCanvas
import destiny.tools.mutableStackOf
import java.util.*

object ModelCanvasExt {

  private fun getLocale(desiredLocale: Locale): Locale {
    return LocaleTools.getBestMatchingLocale(
      desiredLocale,
      listOf(
        Locale.TRADITIONAL_CHINESE,
        Locale.SIMPLIFIED_CHINESE
      )
    ) ?: Locale.TRADITIONAL_CHINESE
  }

  fun IContextModel.getDigestCanvasNormal(desiredLocale: Locale,
                                          viewSettings: ViewSettings = ViewSettings()): ColorCanvas {
    return if (this.hiddenVenusFoes.isEmpty()) {
      getDigestCanvasCore(desiredLocale, viewSettings)
    } else {
      // 7 x 16
      val core = getDigestCanvasCore(desiredLocale, viewSettings)
      // N x 36
      val hiddenFoe = getHiddenFoeCanvas(desiredLocale)
      ColorCanvas(core.height + hiddenFoe.height, maxOf(core.width, hiddenFoe.width)).apply {
        add(core, 1, 1)
        add(hiddenFoe, 8, 1)
      }
    }
  }

  /** 暗金伏斷 canvas */
  fun IContextModel.getHiddenFoeCanvas(desiredLocale: Locale): ColorCanvas {
    val locale = getLocale(desiredLocale)
    val canvases: List<ColorCanvas> = this.hiddenVenusFoes.map { (from, to) ->
      ColorCanvas(1, 36, ChineseStringTools.NULL_CHAR).apply {
        // "日禽昴日雞　犯　月禽心月狐　暗金伏斷"
        setText("""${from.toChineseChar()}禽${this@getHiddenFoeCanvas.getStation(from).getFullName(locale)}""", 1, 1)
        setText("犯", 1, 13)
        setText("""${to.toChineseChar()}禽${this@getHiddenFoeCanvas.getStation(to).getFullName(locale)}""", 1, 17)
        setText("暗金伏斷", 1, 29)
      }
    }
    return ColorCanvas(canvases.size, 36, ChineseStringTools.NULL_CHAR).apply {
      canvases.forEachIndexed { index, cc ->
        add(cc, index + 1, 1)
      }
    }
  }

  /**
   * 四課三傳 , 正常版 , 高度為7 , 寬度 30
   *
   * 　　　初（日）：昴日雞　　　　
   * 　　　中（時）：心月狐　　　　
   * 　　　末（翻）：角木蛟　　　　
   * 活曜　　翻禽　　倒將　　日將　
   * 危月燕　角木蛟　心月狐　昴日雞
   * 心月狐　昴日雞　亢金龍　參水猿
   * 時　　　日　　　月　　　年　　
   *
   * */
  private fun IContextModel.getDigestCanvasCore(desiredLocale: Locale = Locale.TAIWAN,
                                                viewSettings: ViewSettings): ColorCanvas {
    val locale = getLocale(desiredLocale)

    val gray = "#666666"

    fun ColorCanvas.outputLunarStation(ls: LunarStation, x: Int, y: Int) {
      setText(ls.toString(locale), x, y)
      setText(ls.planet.getAbbreviation(locale), x, y + 2, gray)
      setText(ls.animal.toString(locale), x, y + 4, gray)
    }

    return ColorCanvas(7, 30, ChineseStringTools.NULL_CHAR).apply {
      // 初傳：日禽
      setText("初（日）：" + day.getFullName(locale), 1, 7)
      // 中傳：時禽
      setText("中（時）：" + hour.getFullName(locale), 2, 7)
      // 末傳：翻禽
      setText("末（翻）：" + oppo.getFullName(locale), 3, 7)
      4.let { x ->
        val stack = when (viewSettings.direction) {
          Direction.R2L -> mutableStackOf(year, month, day, hour)
          Direction.L2R -> mutableStackOf(hour, day, month, year)
        }
        1.let { y ->
          // 左上：活曜
          setText("活曜", x, y, "green")
          setText(self.toString(locale), x + 1, y, "green")
          setText(self.planet.getAbbreviation(locale), x + 1, y + 2, gray)
          setText(self.animal.toString(locale), x + 1, y + 4, gray)
          // 左下：時/年 禽
          outputLunarStation(stack.pop(), x + 2, y)
          y + 8
        }.let { y ->
          // 左中上：翻禽
          setText("翻禽", x, y, "red")
          setText(oppo.toString(locale), x + 1, y, "red")
          setText(oppo.planet.getAbbreviation(locale), x + 1, y + 2, gray)
          setText(oppo.animal.toString(locale), x + 1, y + 4, gray)
          // 左中下：日/月 禽
          outputLunarStation(stack.pop(), x + 2, y)
          y + 8
        }.let { y ->
          // 右中上：倒將
          setText("倒將", x, y)
          setText(reversed.toString(locale), x + 1, y)
          setText(reversed.planet.getAbbreviation(locale), x + 1, y + 2, gray)
          setText(reversed.animal.toString(locale), x + 1, y + 4, gray)

          // 右中下：月/日 禽
          outputLunarStation(stack.pop(), x + 2, y)
          y + 8
        }.let { y ->
          // 右上：(日)氣將
          setText("氣將", x, y)
          setText(dayIndex.leader().toString(locale), x + 1, y)
          setText(dayIndex.leader().planet.getAbbreviation(locale), x + 1, y + 2, gray)
          setText(dayIndex.leader().animal.toString(locale), x + 1, y + 4, gray)
          // 右下：年/時 禽
          outputLunarStation(stack.pop(), x + 2, y)
        }
        x
      }
        .let { x -> x + 3 }
        .let { x ->
          1.also { y ->
            val txt = when (viewSettings.direction) {
              Direction.R2L -> "時　　　日　　　月　　　年"
              Direction.L2R -> "年　　　月　　　日　　　時"
            }
            setText(txt, x, y, gray)
          }
        }
    }
  }

  /**
   * 四課三傳 , 扁平版 , 高度為4 , 寬度 10 ，類似 通書上方格式
   *  　　昴　　
   *  　　心　　
   *  危角角心昴
   *  心昴　亢參
   * */
  fun IContextModel.getDigestCanvasFlat(desiredLocale: Locale): ColorCanvas {
    val locale = getLocale(desiredLocale)

    return ColorCanvas(4, 10, ChineseStringTools.NULL_CHAR).apply {
      // 左上：活曜
      setText(self.toString(locale), 3, 1, "green")
      // 左下：時禽
      setText(hour.toString(locale), 4, 1)

      // 左中上：翻禽
      setText(oppo.toString(locale), 3, 3, "red")
      // 左中下：日禽
      setText(day.toString(locale), 4, 3)

      // 右中上：時禽
      setText(hour.toString(locale), 3, 7)
      // 右中下：月禽
      setText(month.toString(locale), 4, 7)

      // 右上：日禽
      setText(day.toString(locale), 3, 9)
      // 右下：年禽
      setText(year.toString(locale), 4, 9)

      // 初傳：日禽
      setText(day.toString(locale), 1, 5)
      // 中傳：時禽
      setText(hour.toString(locale), 2, 5)
      // 末傳：翻禽
      setText(oppo.toString(locale), 3, 5)
    }
  }
}
