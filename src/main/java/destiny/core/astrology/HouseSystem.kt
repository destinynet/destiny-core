/**
 * @author smallufo
 * Created on 2007/5/29 at 上午 2:29:09
 */
package destiny.core.astrology

import destiny.tools.ILocaleString
import java.util.*

fun HouseSystem.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.core.astrology.Astrology"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun HouseSystem.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

/**
 * 分宮法 , Zodiac House Systems
 */
enum class HouseSystem(val nameKey: String) {

  PLACIDUS("HouseSystem.PLACIDUS"),
  KOCH("HouseSystem.KOCH"),
  /** 東昇/天頂 度數 均等三等分 , Porphyry */
  PORPHYRIUS("HouseSystem.PORPHYRIUS"),
  REGIOMONTANUS("HouseSystem.REGIOMONTANUS"),
  CAMPANUS("HouseSystem.CAMPANUS"),
  EQUAL("HouseSystem.EQUAL"),
  VEHLOW_EQUAL("HouseSystem.VEHLOW_EQUAL"),
  AXIAL_ROTATION("HouseSystem.AXIAL_ROTATION"),
  HORIZONTAL("HouseSystem.HORIZONTAL"),
  ALCABITIUS("HouseSystem.ALCABITIUS"),
  /**
   * axial rotation system/ Meridian houses
   * this is a non-quadrant system that can be described as both space- and time-based
   *
   * (wiki) Each house is exactly 2 sidereal hours long.
   *  */
  MERIDIAN("HouseSystem.MERIDIAN")
  ;
}
