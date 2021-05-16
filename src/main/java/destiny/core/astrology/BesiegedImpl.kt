/**
 * Created by smallufo on 2017-02-16.
 */
package destiny.core.astrology

import mu.KotlinLogging
import java.io.Serializable


class BesiegedImpl(private val relativeTransitImpl: IRelativeTransit) : IBesieged, Serializable {

  /**
   * 最 Generalized 的介面
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmtJulDay GMT 時間
   * @param otherPlanets 其他行星
   * @param angles 欲計算的交角角度
   * @return 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  override fun getBesiegingPlanetsByDegrees(planet: Planet,
                                            gmtJulDay: Double,
                                            otherPlanets: Collection<Planet>,
                                            angles: Collection<Double>): Pair<IAngleData?, IAngleData?> {

    /**
     * 先逆推，計算此 planet 「之前」與其他行星呈現 交角的最近時間
     */
    val priorAngleData = otherPlanets.asSequence()
      .filter { it !== planet }
      .map { eachOther ->
        relativeTransitImpl.getNearestRelativeTransitGmtJulDay(planet, eachOther, gmtJulDay, angles, false)
          ?.let { (gmt, angle) ->
            AngleData(planet, eachOther, angle, gmt)
          }
      }
      .filterNotNull()
      .sortedBy { it.gmtJulDay }
      .lastOrNull() // 取最大值 (最接近 gmtJulDay)


    logger.trace("之前形成度數 : {}", priorAngleData)


    /**
     * 順推，計算此 planet 「之後」與其他行星呈現交角的時間
     */
    val afterAngleData = otherPlanets.asSequence()
      .filter { it !== planet }
      .let { seqOfPlanets ->
        if (priorAngleData != null)
          seqOfPlanets.filter { other -> other !== priorAngleData.points.first { it != planet } } // 將之前形成交角的行星，移除之後搜尋的範圍中（只有月亮有此情形）
        else
          seqOfPlanets
      }
      .map { eachOther ->
        relativeTransitImpl.getNearestRelativeTransitGmtJulDay(planet, eachOther, gmtJulDay, angles, true)
          ?.let { (gmt, angle) ->
            AngleData(planet, eachOther, angle, gmt)
          }
      }
      .filterNotNull()
      .sortedBy { it.gmtJulDay }
      .firstOrNull()  // 取最小值 (最接近 gmtJulDay)


    logger.trace("之後形成度數 : {}", afterAngleData)

    return priorAngleData to afterAngleData
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
