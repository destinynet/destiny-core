/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.Centric.GEO
import destiny.core.astrology.Coordinate.ECLIPTIC
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.GmtJulDay
import kotlin.math.abs

/**
 * 顯現（[PhasisOptions]）：
 *
 * - 停滯，[PhasisOptions.StationBy.WINDOW]：窗的兩端（出生前後 [PhasisOptions.days] 天）速度一正一負，窗內就有一次停滯，
 *   再以 [retrogradeImpl] 從窗的起點往後找出確切時刻。速度同號就不搜尋 —— 大多數星都在這一步結束
 * - 停滯，[PhasisOptions.StationBy.SPEED]：出生那一刻的速度不超過平均日速的 [PhasisOptions.speedRatio]，取前後最近的那次停滯
 * - 偕日升落：與太陽的黃經距離減去光束半徑，在窗內每半天取樣一次，變號處以二分法找到時刻。
 *   距離在出生那一刻就離界線很遠（以每天 3° 的相對速度在窗內也到不了）的星不取樣
 */
class PhasisImpl(
  private val starPositionImpl: IStarPosition<*>,
  private val retrogradeImpl: IRetrograde,
  val options: PhasisOptions = PhasisOptions.HELLENISTIC,
) : IPhasis {

  private fun pos(star: Star, t: GmtJulDay) = starPositionImpl.getPosition(star, t, GEO, ECLIPTIC, StarTypeOptions.MEAN)

  override fun getPhases(h: IHoroscopeModel, planet: Planet): List<Phasis> {
    if (planet !in options.planets) return emptyList()
    val t0 = h.gmtJulDay
    return (stations(planet, t0) + heliacal(planet, t0)).sortedBy { it.days }
  }

  override fun getResults(h: IHoroscopeModel): List<Phasis> =
    Planet.classicalList.flatMap { getPhases(h, it) }

  private fun stations(planet: Planet, t0: GmtJulDay): List<Phasis> {
    val wanted = options.types.filter { it == Phasis.Type.STATION_RETROGRADE || it == Phasis.Type.STATION_DIRECT }
    if (wanted.isEmpty()) return emptyList()
    val s: Stationary = when (options.stationBy) {
      PhasisOptions.StationBy.WINDOW -> {
        val from = t0 - options.days
        val to = t0 + options.days
        if (pos(planet, from).speedLng * pos(planet, to).speedLng > 0) return emptyList()
        retrogradeImpl.getNextStationary(planet, from, true, starPositionImpl).takeIf { it.gmtJulDay <= to } ?: return emptyList()
      }
      PhasisOptions.StationBy.SPEED  -> {
        val avg = AverageDailyMotionMap.getAvgDailySpeed(planet) ?: return emptyList()
        if (abs(pos(planet, t0).speedLng) > avg * options.speedRatio) return emptyList()
        val next = retrogradeImpl.getNextStationary(planet, t0, true, starPositionImpl)
        val prev = retrogradeImpl.getNextStationary(planet, t0, false, starPositionImpl)
        listOf(next, prev).minBy { abs(it.gmtJulDay - t0) }
      }
    }
    val type = when (s.type) {
      StationaryType.DIRECT_TO_RETROGRADE -> Phasis.Type.STATION_RETROGRADE
      StationaryType.RETROGRADE_TO_DIRECT -> Phasis.Type.STATION_DIRECT
    }
    if (type !in wanted) return emptyList()
    return listOf(phasis(planet, type, s.gmtJulDay, t0))
  }

  private fun heliacal(planet: Planet, t0: GmtJulDay): List<Phasis> {
    if (Phasis.Type.EMERGING !in options.types && Phasis.Type.SINKING !in options.types) return emptyList()
    // 帶號：正 ＝ 在光束外、負 ＝ 在光束內
    fun f(t: GmtJulDay): Double = pos(planet, t).lng.toZodiacDegree().getAngle(pos(Planet.SUN, t).lng.toZodiacDegree()) - options.beams
    if (abs(f(t0)) > MAX_RELATIVE_SPEED * options.days) return emptyList()

    val steps = (options.days * 2 / STEP).toInt()
    val result = mutableListOf<Phasis>()
    var ta = t0 - options.days
    var fa = f(ta)
    for (i in 1..steps) {
      val tb = t0 - options.days + i * STEP
      val fb = f(tb)
      if (fa == 0.0 || fa * fb < 0) {
        val t = bisect(::f, ta, tb, fa)
        val type = if (fb > fa) Phasis.Type.EMERGING else Phasis.Type.SINKING
        if (type in options.types) result += phasis(planet, type, t, t0)
      }
      ta = tb
      fa = fb
    }
    return result
  }

  private fun bisect(f: (GmtJulDay) -> Double, a0: GmtJulDay, b0: GmtJulDay, fa0: Double): GmtJulDay {
    var a = a0
    var b = b0
    var fa = fa0
    while (b - a > 1e-4) {
      val m = a + (b - a) / 2
      val fm = f(m)
      if (fa * fm <= 0) b = m else { a = m; fa = fm }
    }
    return a + (b - a) / 2
  }

  private fun phasis(planet: Planet, type: Phasis.Type, t: GmtJulDay, t0: GmtJulDay): Phasis {
    val deg = pos(planet, t).lng.toZodiacDegree()
    val sun = pos(Planet.SUN, t).lng.toZodiacDegree()
    return Phasis(planet, type, t - t0, deg, deg.isOriental(sun), beams = if (type == Phasis.Type.EMERGING || type == Phasis.Type.SINKING) options.beams else null)
  }

  companion object {
    /** 取樣間隔（日）：水星與太陽的相對速度至多約每天 2.5°，半天內不會穿過界線兩次 */
    private const val STEP = 0.5
    /** 與太陽的相對速度上限（度／日），用來略過窗內不可能穿過光束界線的星 */
    private const val MAX_RELATIVE_SPEED = 3.0
  }
}
