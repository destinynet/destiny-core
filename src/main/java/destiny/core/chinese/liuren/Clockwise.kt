/**
 * Created by smallufo on 2021-09-21.
 */
package destiny.core.chinese.liuren

import destiny.core.Descriptive
import java.util.*


/**
 * 推算貴神
 */
enum class Clockwise {
  DayNightFixed,
  XinRenKuiReverse
}

fun Clockwise.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      Clockwise.DayNightFixed    -> "晝順夜逆"
      Clockwise.XinRenKuiReverse -> "辛壬癸 晝逆夜順"
    }
  }

  override fun getDescription(locale: Locale): String {
    return when (this@asDescriptive) {
      Clockwise.DayNightFixed    -> "固定為晝順夜逆。"
      Clockwise.XinRenKuiReverse -> "甲乙丙丁戊己庚 皆為晝順夜逆； 辛壬癸 為晝逆夜順。"
    }
  }


}
