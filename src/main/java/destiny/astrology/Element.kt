/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 2:42:41
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*


fun Element.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.astrology.Sign"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun Element.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

/**
 * 四大元素 : 火 Fire , 土 Earth , 風 Air , 水 Water
 */
enum class Element(val nameKey: String)  {
  /** 火  */
  FIRE("Element.FIRE"),
  /** 土  */
  EARTH("Element.EARTH"),
  /** 風  */
  AIR("Element.AIR"),
  /** 水  */
  WATER("Element.WATER");
}
