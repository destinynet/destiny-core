/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 2:57:16
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

/**
 * 基本 Cardinal , 固定 Fixed , 變動 Mutable
 */
enum class Quality(private val nameKey: String) : ILocaleString {

  /** 基本 : 辰戌丑未 */
  CARDINAL("Quality.CARDINAL"),

  /** 固定 : 子午卯酉 */
  FIXED("Quality.FIXED"),

  /** 變動 : 寅巳申亥 */
  MUTABLE("Quality.MUTABLE");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private const val resource = "destiny.astrology.Sign"
  }

}
