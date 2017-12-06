/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology.eclipse

open class SolarEclipseTotal(max: Double, partialBegin: Double, partialEnd: Double, val totalBegin: Double, val totalEnd: Double) : AbstractSolarEclipse(max, partialBegin, partialEnd) {

  override val solarType: AbstractSolarEclipse.SolarType
    get() = AbstractSolarEclipse.SolarType.TOTAL

  override fun toString(): String {
    return "[全食 " +
      "begin=" + begin +
      ", totalBegin=" + totalBegin +
      ", max=" + max +
      ", totalEnd=" + totalEnd +
      ", end=" + end + ']'
  }

}
