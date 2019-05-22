/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.astrology

sealed class Rsmi(nameKey: String, abbrKey: String) : Point(nameKey, Rsmi::class.java.name , abbrKey) {

  object RISING   : Rsmi("Rsmi.RISING"  , "Rsmi.RISING_ABBR")
  object SETTING  : Rsmi("Rsmi.SETTING" , "Rsmi.SETTING_ABBR")
  object MERIDIAN : Rsmi("Rsmi.MERIDIAN", "Rsmi.MERIDIAN_ABBR")
  object NADIR    : Rsmi("Rsmi.NADIR"   , "Rsmi.NADIR_ABBR")

  companion object {
    val array by lazy { arrayOf(RISING, SETTING, MERIDIAN, NADIR) }
    val list by lazy { listOf(*array)}
  }
}