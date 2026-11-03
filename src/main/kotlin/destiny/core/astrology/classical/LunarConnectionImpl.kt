/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 月亮的離與合，以盤面時刻的位置與黃經速度線性外推（月亮兩天半內的行程，外推的誤差在角分以內；不需要星曆）。
 * 速度不明時用平均日速（[AverageDailyMotionMap]）。對象是其餘六顆古典行星；同一顆星的不同相位各自算，取時間最近的那一個
 */
class LunarConnectionImpl(
  val options: LunarConnectionOptions = LunarConnectionOptions.HELLENISTIC,
) : ILunarConnection {

  private fun speed(h: IHoroscopeModel, p: Planet): Double? =
    h.getStarPosition(p)?.speedLng ?: AverageDailyMotionMap.getAvgDailySpeed(p)

  override fun getConnection(h: IHoroscopeModel): LunarConnection? {
    val moon = h.positionMap[Planet.MOON]?.lngDeg ?: return null
    val vMoon = speed(h, Planet.MOON) ?: return null
    if (vMoon <= 0) return null
    val degInSign = moon.value.mod(30.0)

    val links = (Planet.classicalList - Planet.MOON).flatMap { p ->
      val deg = h.positionMap[p]?.lngDeg ?: return@flatMap emptyList()
      val v = vMoon - (speed(h, p) ?: return@flatMap emptyList())
      val r = (moon.value - deg.value).mod(360.0)
      options.aspects.flatMap { a ->
        targets(a).flatMap { t ->
          val forward = (t - r).mod(360.0)
          val backward = (r - t).mod(360.0)
          listOf(
            Candidate(false, LunarConnection.Link(p, a, forward, forward / v)),
            Candidate(true, LunarConnection.Link(p, a, backward, backward / v)),
          )
        }
      }
    }.filter { c ->
      val travel = c.link.days * vMoon
      travel <= options.window && (!options.withinSign || if (c.past) travel <= degInSign else travel < 30.0 - degInSign)
    }

    val separating = links.filter { it.past && it.link.orb > 0 }.minByOrNull { it.link.days }?.link
    val applying = links.filter { !it.past }.minByOrNull { it.link.days }?.link
    return if (separating == null && applying == null) null else LunarConnection(separating, applying)
  }

  private data class Candidate(val past: Boolean, val link: LunarConnection.Link)

  /** 月亮減去對方的黃經（0–360）在準確時應為哪些值 */
  private fun targets(a: Aspect): List<Double> = when (a.degree) {
    0.0, 180.0 -> listOf(a.degree)
    else       -> listOf(a.degree, 360.0 - a.degree)
  }
}
