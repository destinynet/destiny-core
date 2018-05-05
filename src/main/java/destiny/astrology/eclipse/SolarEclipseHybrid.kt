/**
 * Created by smallufo on 2017-11-11.
 */
package destiny.astrology.eclipse

/** 全環食  */
class SolarEclipseHybrid(max: Double,
                         partialBegin: Double,
                         partialEnd: Double,
                         totalBegin: Double,
                         totalEnd: Double,
                         centerBegin: Double,
                         centerEnd: Double,
                         annularBegin: Double,
                         annularEnd: Double) :
  SolarEclipseAnnularCentered(max, partialBegin, partialEnd, totalBegin, totalEnd, centerBegin, centerEnd, annularBegin,
                              annularEnd) {

  override val solarType: ISolarEclipse.SolarType
    get() = ISolarEclipse.SolarType.HYBRID

}
