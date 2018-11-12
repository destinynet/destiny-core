/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 5:40:57
 */
package destiny.astrology.classical

import destiny.tools.ILocaleString
import java.util.*

/**
 * 行星落入星座的 , 旺 廟 陷 落
 */
enum class Dignity(private val nameKey: String) : ILocaleString {

  /** 旺 (+5) , 守護  */
  RULER("Dignity.RULER"),

  /** 廟 (+4) , 躍升  */
  EXALTATION("Dignity.EXALTATION"),

  /** 三分性 (+3) */
  TRIPLICITY("Dignity.TRIPLICITY"),

  /** 界 (+2) */
  TERM("Dignity.TERM"),

  /** 面 (+1) */
  FACE("Dignity.FACE"),

  /** 落 (-4)  */
  FALL("Dignity.FALL"),

  /** 陷 (-5)  */
  DETRIMENT("Dignity.DETRIMENT");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private const val resource = "destiny.astrology.classical.Classical"
  }
}
