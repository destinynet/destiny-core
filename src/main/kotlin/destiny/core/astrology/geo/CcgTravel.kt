/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay

/**
 * 行運線壓過某城市的一段時間窗口（設計 doc 5.4）。
 *
 * [enter] / [exit] 為 null 表示窗口頂到查詢區間邊界（開始前已在 orb 內 / 結束仍在 orb 內）。
 * 同一 (planet, angle) 因逆行可產生多個 hit。
 */
data class TransitLineHit(
  val planet: Planet,
  val angle: GeoAngle,
  val enter: GmtJulDay?,
  val exit: GmtJulDay?,
  val peak: GmtJulDay,
  val peakOrbDeg: Double,
)

/**
 * 窗口切割：在 [grid] 取樣 [distance]，`d(t) ≤ orbDeg` 的連續 run 各成一個 hit；
 * 邊界二分求根、峰值三分法細化。純函數 —— [distance] 回 null 視為不在 orb 內。
 *
 * [grid] 須遞增且至少兩點；取樣密度由呼叫端保證不跳過事件
 * （火星 α 日漂移 ≤0.5°，1 天步長已足）。
 */
fun segmentHits(
  planet: Planet,
  angle: GeoAngle,
  grid: List<GmtJulDay>,
  orbDeg: Double,
  distance: (GmtJulDay) -> Double?,
): List<TransitLineHit> {
  require(grid.size >= 2) { "grid needs at least 2 samples" }
  require(orbDeg > 0) { "orbDeg must be positive" }

  fun d(t: GmtJulDay): Double = distance(t) ?: Double.MAX_VALUE

  val ds = grid.map(::d)
  val hits = mutableListOf<TransitLineHit>()
  var i = 0
  while (i < grid.size) {
    if (ds[i] > orbDeg) {
      i++
      continue
    }
    var j = i
    while (j + 1 < grid.size && ds[j + 1] <= orbDeg) j++

    val enter = if (i == 0) null else crossing(grid[i - 1], grid[i], orbDeg, ::d)
    val exit = if (j == grid.size - 1) null else crossing(grid[j], grid[j + 1], orbDeg, ::d)

    // 峰值：run 內最低樣本的鄰域做三分法
    val k = (i..j).minBy { ds[it] }
    var lo = grid[(k - 1).coerceAtLeast(0)].value
    var hi = grid[(k + 1).coerceAtMost(grid.size - 1)].value
    repeat(80) {
      val m1 = lo + (hi - lo) / 3
      val m2 = hi - (hi - lo) / 3
      if (d(GmtJulDay(m1)) <= d(GmtJulDay(m2))) hi = m2 else lo = m1
    }
    val refined = GmtJulDay((lo + hi) / 2)
    val (peak, peakOrb) = if (d(refined) <= ds[k]) refined to d(refined) else grid[k] to ds[k]

    hits.add(TransitLineHit(planet, angle, enter, exit, peak, peakOrb))
    i = j + 1
  }
  return hits
}

/** 在 (a, b) 之間二分求 d(t) = orb 的交點；呼叫端保證兩端一內一外 */
private fun crossing(a: GmtJulDay, b: GmtJulDay, orbDeg: Double, d: (GmtJulDay) -> Double): GmtJulDay {
  val aInside = d(a) <= orbDeg
  var lo = a.value
  var hi = b.value
  repeat(50) {
    val mid = (lo + hi) / 2
    val midInside = d(GmtJulDay(mid)) <= orbDeg
    if (midInside == aInside) lo = mid else hi = mid
  }
  return GmtJulDay((lo + hi) / 2)
}
