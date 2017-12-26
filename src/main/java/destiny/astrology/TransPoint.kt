/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 04:22:40
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

/**
 * 四個角點，天底、東昇點、天頂、西落
 */
enum class TransPoint(private val nameKey: String) : ILocaleString {

  /** 東昇  */
  RISING("TransPoint.RISING"),
  /** 天頂  */
  MERIDIAN("TransPoint.MERIDIAN"),
  /** 西落  */
  SETTING("TransPoint.SETTING"),
  /** 天底  */
  NADIR("TransPoint.NADIR");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private const val resource = "destiny.astrology.Star"
  }


}
