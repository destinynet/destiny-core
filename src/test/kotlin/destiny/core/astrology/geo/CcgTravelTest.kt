/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.Planet.JUPITER
import destiny.core.astrology.Planet.MARS
import destiny.core.calendar.GmtJulDay
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * B1 窗口切割純函數測試（設計 doc 5.4）：用合成距離函數釘死
 * enter/exit 二分求根與 peak 三分法的正確性。
 */
class CcgTravelTest {

  private fun grid(from: Double, to: Double, step: Double = 1.0): List<GmtJulDay> =
    generateSequence(from) { it + step }.takeWhile { it <= to + 1e-9 }.map { GmtJulDay(it) }.toList()

  /** V 型（線性進出，斜率 2°/天，orb 5°）→ 單一 hit，enter/exit 距谷底 ±2.5 天 */
  @Test
  fun testVShape() {
    val t0 = 2451560.0
    val hits = segmentHits(MARS, GeoAngle.MC, grid(2451550.0, 2451570.0), 5.0) { t ->
      (t.value - t0).absoluteValue * 2.0
    }
    assertEquals(1, hits.size)
    val hit = hits[0]
    assertEquals(MARS, hit.planet)
    assertEquals(GeoAngle.MC, hit.angle)
    assertEquals(t0 - 2.5, hit.enter!!.value, 1e-3)
    assertEquals(t0 + 2.5, hit.exit!!.value, 1e-3)
    assertEquals(t0, hit.peak.value, 1e-3)
    assertEquals(0.0, hit.peakOrbDeg, 1e-3)
  }

  /** W 型雙谷（模擬逆行回訪）→ 兩個 hit，各自窗口正確 */
  @Test
  fun testDoubleDip() {
    val a = 2451555.0
    val b = 2451565.0
    val hits = segmentHits(JUPITER, GeoAngle.ASC, grid(2451550.0, 2451570.0), 4.0) { t ->
      min((t.value - a).absoluteValue, (t.value - b).absoluteValue) * 2.0
    }
    assertEquals(2, hits.size)
    assertEquals(a - 2.0, hits[0].enter!!.value, 1e-3)
    assertEquals(a + 2.0, hits[0].exit!!.value, 1e-3)
    assertEquals(a, hits[0].peak.value, 1e-3)
    assertEquals(b - 2.0, hits[1].enter!!.value, 1e-3)
    assertEquals(b + 2.0, hits[1].exit!!.value, 1e-3)
    assertEquals(b, hits[1].peak.value, 1e-3)
  }

  /** 全程都在 orb 內（慢星準靜態）→ 單一 hit，enter/exit 皆 null */
  @Test
  fun testAlwaysInsideOrb() {
    val hits = segmentHits(JUPITER, GeoAngle.MC, grid(2451550.0, 2451570.0), 5.0) { 1.0 }
    assertEquals(1, hits.size)
    assertNull(hits[0].enter)
    assertNull(hits[0].exit)
    assertEquals(1.0, hits[0].peakOrbDeg, 1e-9)
  }

  /** 全程在 orb 外 → 空 */
  @Test
  fun testAlwaysOutsideOrb() {
    assertTrue(segmentHits(MARS, GeoAngle.DESC, grid(2451550.0, 2451570.0), 5.0) { 9.0 }.isEmpty())
  }

  /** 區間開始時已在 orb 內、途中離開 → enter=null，exit 精確 */
  @Test
  fun testStartsInsideOrb() {
    val from = 2451550.0
    val hits = segmentHits(MARS, GeoAngle.IC, grid(from, 2451570.0), 5.0) { t ->
      (t.value - from) * 2.0
    }
    assertEquals(1, hits.size)
    assertNull(hits[0].enter)
    assertEquals(from + 2.5, hits[0].exit!!.value, 1e-3)
    assertEquals(from, hits[0].peak.value, 1e-3)
    assertEquals(0.0, hits[0].peakOrbDeg, 1e-3)
  }

  /** 距離函數全程回 null（不可算）→ 空，不丟例外 */
  @Test
  fun testAllNullDistance() {
    assertTrue(segmentHits(MARS, GeoAngle.ASC, grid(2451550.0, 2451570.0), 5.0) { null }.isEmpty())
  }
}
