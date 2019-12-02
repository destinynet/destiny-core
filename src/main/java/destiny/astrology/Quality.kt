/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 2:57:16
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

fun Quality.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.astrology.Sign"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun Quality.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

/**
 * 基本 Cardinal , 固定 Fixed , 變動 Mutable
 */
enum class Quality(val nameKey: String) {

  /** 基本 : 辰戌丑未 */
  CARDINAL("Quality.CARDINAL"),

  /** 固定 : 子午卯酉 */
  FIXED("Quality.FIXED"),

  /** 變動 : 寅巳申亥 */
  MUTABLE("Quality.MUTABLE");
}
