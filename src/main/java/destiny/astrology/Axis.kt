/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.astrology

sealed class Axis(nameKey: String, abbrKey: String) : Point(nameKey, Axis::class.java.name , abbrKey) {

  object RISING   : Axis("Axis.RISING"  , "Axis.RISING_ABBR")
  object SETTING  : Axis("Axis.SETTING" , "Axis.SETTING_ABBR")
  object MERIDIAN : Axis("Axis.MERIDIAN", "Axis.MERIDIAN_ABBR")
  object NADIR    : Axis("Axis.NADIR"   , "Axis.NADIR_ABBR")

  companion object {
    val array by lazy { arrayOf(RISING, SETTING, MERIDIAN, NADIR) }
    val list by lazy { listOf(*array)}
  }
}
