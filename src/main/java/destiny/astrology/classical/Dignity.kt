/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 5:40:57
 */
package destiny.astrology.classical

import destiny.tools.ILocaleString
import java.util.*

fun Dignity.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.astrology.classical.Classical"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun Dignity.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

/**
 * 行星落入星座的 , 廟 旺 陷 落
 */
enum class Dignity(val nameKey: String) : Comparator<Dignity> {

  /** 廟 (+5) , 守護  */
  RULER("Dignity.RULER"),

  /** 旺 (+4) , 躍升  */
  EXALTATION("Dignity.EXALTATION"),

  /** 三分性 (+3) , 三分主星 */
  TRIPLICITY("Dignity.TRIPLICITY"),

  /** 界 (+2) , 六度守護 */
  TERM("Dignity.TERM"),

  /** 面 (+1) , 十度守護 */
  FACE("Dignity.FACE"),

  /** 落 (-4) , 失利 , 某派認為此情形比 [DETRIMENT] 更糟 */
  FALL("Dignity.FALL"),

  /** 陷 (-5) , 自己守護星座的對立面 */
  DETRIMENT("Dignity.DETRIMENT");

  override fun compare(o1: Dignity?, o2: Dignity?): Int {
    return values().let { array ->
      array.indexOf(o1) - array.indexOf(o2)
    }
  }
}
