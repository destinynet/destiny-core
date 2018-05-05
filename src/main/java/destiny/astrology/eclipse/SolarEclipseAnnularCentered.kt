/**
 * Created by smallufo on 2017-11-08.
 */
package destiny.astrology.eclipse

open class SolarEclipseAnnularCentered(max: Double,
                                       partialBegin: Double,
                                       partialEnd: Double,
                                       totalBegin: Double,
                                       totalEnd: Double,
                                       override val centerBegin: Double,
                                       override val centerEnd: Double,
                                       annularBegin: Double,
                                       annularEnd: Double) :
  SolarEclipseAnnular(max, partialBegin, partialEnd, totalBegin, totalEnd, annularBegin, annularEnd), IEclipseCenter
