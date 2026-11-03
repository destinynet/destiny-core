/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree

/**
 * 以與太陽的黃經距離分三圈（[CombustionOptions]）：日心嚴格小於 [CombustionOptions.cazimi]；
 * 灼傷大於日心、到 [CombustionOptions.combust] 為止（含）；光束下大於灼傷、到 [CombustionOptions.underBeams] 為止（含）。
 * 正好等於日心界線的那一點不屬於任何一圈（沿用先前 prompt 判斷的邊界）。只看盤面時刻的位置與速度，不需要星曆
 */
class CombustionImpl(
  val options: CombustionOptions = CombustionOptions.LILLY,
) : ICombustion {

  override fun getCombustion(h: IHoroscopeModel, planet: Planet): Combustion? {
    if (planet === Planet.SUN || planet !in options.planets) return null
    val sunDeg = h.positionMap[Planet.SUN]?.lngDeg ?: return null
    val deg = h.positionMap[planet]?.lngDeg ?: return null
    val distance = deg.getAngle(sunDeg)
    val zone = when {
      distance < options.cazimi                                 -> Combustion.Zone.CAZIMI
      distance > options.cazimi && distance <= options.combust   -> Combustion.Zone.COMBUST
      distance > options.combust && distance <= options.underBeams -> Combustion.Zone.UNDER_BEAMS
      else                                                       -> return null
    }
    return Combustion(planet, zone, distance, approaching(h, planet, deg.value, sunDeg.value), options.limits)
  }

  override fun getResults(h: IHoroscopeModel): List<Combustion> =
    options.planets.mapNotNull { getCombustion(h, it) }.sortedBy { it.distance }

  /** 一小段時間後兩者的距離會不會變小：以兩顆星的黃經速度外推。速度不明為 null */
  private fun approaching(h: IHoroscopeModel, planet: Planet, deg: Double, sunDeg: Double): Boolean? {
    val v = h.getStarPosition(planet)?.speedLng ?: return null
    val vs = h.getStarPosition(Planet.SUN)?.speedLng ?: return null
    val dt = 0.01
    val later = (deg + v * dt).toZodiacDegree().getAngle((sunDeg + vs * dt).toZodiacDegree())
    return later < deg.toZodiacDegree().getAngle(sunDeg.toZodiacDegree())
  }
}
