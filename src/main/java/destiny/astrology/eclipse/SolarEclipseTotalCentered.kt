/**
 * Created by smallufo on 2017-11-08.
 */
package destiny.astrology.eclipse

class SolarEclipseTotalCentered(max: Double, partialBegin: Double, partialEnd: Double, totalBegin: Double, totalEnd: Double, override val centerBegin: Double, override val centerEnd: Double) : SolarEclipseTotal(max, partialBegin, partialEnd, totalBegin, totalEnd), IEclipseCenter
