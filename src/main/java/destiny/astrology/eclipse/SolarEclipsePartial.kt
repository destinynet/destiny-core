/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology.eclipse

class SolarEclipsePartial(max: Double, begin: Double, end: Double) : AbstractSolarEclipse(max, begin, end) {

  override val solarType: AbstractSolarEclipse.SolarType
    get() = AbstractSolarEclipse.SolarType.PARTIAL

  override fun toString(): String {
    return "[偏食 begin=$begin, max=$max, end=$end]"
  }
}
