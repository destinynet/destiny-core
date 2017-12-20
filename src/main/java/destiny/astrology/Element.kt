/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 2:42:41
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

/**
 * 四大元素 : 火 Fire , 土 Earth , 風 Air , 水 Water
 */
enum class Element(private val nameKey: String) : ILocaleString {
  /** 火  */
  FIRE("Element.FIRE"),
  /** 土  */
  EARTH("Element.EARTH"),
  /** 風  */
  AIR("Element.AIR"),
  /** 水  */
  WATER("Element.WATER");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private val resource = "destiny.astrology.Sign"
  }


}
