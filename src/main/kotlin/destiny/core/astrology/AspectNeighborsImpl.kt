/**
 * Created by smallufo on 2017-02-16.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.tools.KotlinLogging


class AspectNeighborsImpl(private val relativeTransitImpl: IRelativeTransit) : IAspectNeighbors {

  override fun getNeighborsByDegrees(planet: Planet,
                                     gmtJulDay: GmtJulDay,
                                     otherPlanets: Set<Planet>,
                                     angles: Set<Double>): Pair<IAngleData?, IAngleData?> {

    /**
     * 先逆推，計算此 planet 「之前」與其他行星呈現 交角的最近時間
     */
    val priorAngleData = otherPlanets.asSequence()
      .filter { it !== planet }.mapNotNull { eachOther ->
        relativeTransitImpl.getNearestRelativeTransitGmtJulDay(planet, eachOther, gmtJulDay, angles, false, StarTypeOptions.MEAN)
          ?.let { (gmt, angle) ->
            AngleData(planet, eachOther, angle, gmt)
          }
      }.maxByOrNull { it.gmtJulDay } // 取最大值 (最接近 gmtJulDay)


    logger.trace("之前形成度數 : {}", priorAngleData)


    /**
     * 順推，計算此 planet 「之後」與其他行星呈現交角的時間
     */
    val afterAngleData = otherPlanets.asSequence()
      .filter { it !== planet }.mapNotNull { eachOther ->
        relativeTransitImpl.getNearestRelativeTransitGmtJulDay(planet, eachOther, gmtJulDay, angles, true, StarTypeOptions.MEAN)
          ?.let { (gmt, angle) ->
            AngleData(planet, eachOther, angle, gmt)
          }
      }.minByOrNull { it.gmtJulDay }  // 取最小值 (最接近 gmtJulDay)


    logger.trace("之後形成度數 : {}", afterAngleData)

    return priorAngleData to afterAngleData
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
