/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology.eclipse

open class SolarEclipseAnnular(max: Double, partialBegin: Double, partialEnd: Double, totalBegin: Double, totalEnd: Double, override val annularBegin: Double, override val annularEnd: Double) : SolarEclipseTotal(max, partialBegin, partialEnd, totalBegin, totalEnd), IEclipseAnnular {

  override val solarType: AbstractSolarEclipse.SolarType
    get() = AbstractSolarEclipse.SolarType.ANNULAR


  override fun toString(): String {
    return ("[環食 "
      + "begin=" + begin
      + ", totalBegin=" + totalBegin
      + ", max=" + max
      + ", totalEnd=" + totalEnd
      + ", end=" + end + ']')
  }
}
