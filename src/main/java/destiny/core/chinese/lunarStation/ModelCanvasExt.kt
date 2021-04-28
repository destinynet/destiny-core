package destiny.core.chinese.lunarStation

import destiny.core.astrology.toString
import destiny.tools.ChineseStringTools
import destiny.tools.LocaleTools
import destiny.tools.canvas.ColorCanvas
import java.util.*

/**
 * Created by smallufo on 2021-04-02.
 */
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

  fun IContextModel.getDigestCanvasNormal(desiredLocale: Locale): ColorCanvas {
    return if (this.hiddenVenusFoes.isEmpty()) {
      getDigestCanvasCore(desiredLocale)
    } else {
      // 5 x 14
      val core = getDigestCanvasCore(desiredLocale)
      // N x 36
      val hiddenFoe = getHiddenFoeCanvas(desiredLocale)
      ColorCanvas(core.height + hiddenFoe.height, maxOf(core.width, hiddenFoe.width)).apply {
        add(core , 1 , 1)
        add(hiddenFoe , 6 , 1)
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
   * 四課三傳 , 正常版 , 高度為5 , 寬度 14
   * 　初：昴日雞　
   * 　中：心月狐　
   * 　末：角木蛟　
   * 危　角　心　昴
   * 心　昴　亢　參
   * */
  private fun IContextModel.getDigestCanvasCore(desiredLocale: Locale): ColorCanvas {
    val locale = getLocale(desiredLocale)

    return ColorCanvas(5, 14, ChineseStringTools.NULL_CHAR).apply {
      // 初傳：日禽
      setText("初：" + day.getFullName(locale), 1, 3)
      // 中傳：時禽
      setText("中：" + hour.getFullName(locale), 2, 3)
      // 末傳：翻禽
      setText("末：" + oppo.getFullName(locale), 3, 3)
      4.also { x ->
        1.let { y ->
          // 左上：活曜
          setText(self.toString(locale), x, y, "green")
          // 左下：時禽
          setText(hour.toString(locale), x + 1, y)
          y + 4
        }.let { y ->
          // 左中上：翻禽
          setText(oppo.toString(locale), x, y, "red")
          // 左中下：日禽
          setText(day.toString(locale), x + 1, y)
          y + 4
        }.let { y ->
          // 右中上：時禽
          setText(hour.toString(locale), x, y)
          // 右中下：月禽
          setText(month.toString(locale), x + 1, y)
          y + 4
        }.let { y ->
          // 右上：日禽
          setText(day.toString(locale), x, y)
          // 右下：年禽
          setText(year.toString(locale), x + 1, y)
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
