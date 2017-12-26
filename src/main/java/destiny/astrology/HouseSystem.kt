/**
 * @author smallufo
 * Created on 2007/5/29 at 上午 2:29:09
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

/**
 * 分宮法 , Zodiac House Systems
 */
enum class HouseSystem(private val nameKey: String) : ILocaleString {

  PLACIDUS("HouseSystem.PLACIDUS"),
  KOCH("HouseSystem.KOCH"),
  /** 東昇/天頂 均等三等分 */
  PORPHYRIUS("HouseSystem.PORPHYRIUS"),
  REGIOMONTANUS("HouseSystem.REGIOMONTANUS"),
  CAMPANUS("HouseSystem.CAMPANUS"),
  EQUAL("HouseSystem.EQUAL"),
  VEHLOW_EQUAL("HouseSystem.VEHLOW_EQUAL"),
  AXIAL_ROTATION("HouseSystem.AXIAL_ROTATION"),
  HORIZONTAL("HouseSystem.HORIZONTAL"),
  ALCABITIUS("HouseSystem.ALCABITIUS");

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
