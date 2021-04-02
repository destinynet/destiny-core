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

  /** 四課三傳 , 扁平版 , 高度為4，適用於 line header*/
  fun IContextModel.getDigestCanvasFlat(desiredLocale: Locale) : ColorCanvas {
    val locale = LocaleTools.getBestMatchingLocale(
      desiredLocale,
      listOf(
        Locale.TRADITIONAL_CHINESE,
        Locale.SIMPLIFIED_CHINESE
      )
    ) ?: Locale.TRADITIONAL_CHINESE

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
      // 又中下：月禽
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
