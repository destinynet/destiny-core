/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse

@Deprecated("ISolarEclipse")
abstract class AbstractSolarEclipse(max: Double, begin: Double, end: Double) : AbstractEclipse(begin, max, end) {


  abstract val solarType: ISolarEclipse.SolarType


}
