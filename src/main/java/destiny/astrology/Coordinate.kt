/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:24:30
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

/**
 * Zodiac selection <br></br>
 * 黃道(ECLIPTIC) / 赤道(TROPICAL) / 恆星(SIDEREAL) 座標系 參數
 */
enum class Coordinate(private val nameKey: String) : ILocaleString {

  /** 黃道座標系  */
  ECLIPTIC("Coordinate.ECLIPTIC"),
  /** 赤道座標系  */
  EQUATORIAL("Coordinate.EQUATORIAL"),
  /** 恆星座標系  */
  SIDEREAL("Coordinate.SIDEREAL");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private val resource = "destiny.astrology.Astrology"
  }

}
