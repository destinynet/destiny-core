/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse

abstract class AbstractSolarEclipse(max: Double, begin: Double, end: Double) : AbstractEclipse(begin, max, end) {


  abstract val solarType: SolarType

  enum class SolarType {
    PARTIAL,
    TOTAL,
    ANNULAR,
    HYBRID    // 極為罕見的「全環食」
  }


}
