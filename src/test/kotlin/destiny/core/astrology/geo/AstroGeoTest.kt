/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.EquatorialPos
import destiny.core.astrology.Planet.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.LatLng
import destiny.core.calendar.Lng.Companion.toLng
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.*

class AstroGeoTest {

  /** 建立測試用 context：兩個 basis 共用同一組 (α, δ)，方便驗證幾何本身 */
  private fun ctx(gmstDeg: Double, vararg entries: Triple<AstroPoint, Double, Double>): AstroGeoContext =
    AstroGeoContext(
      GmtJulDay(2451545.0),
      gmstDeg,
      entries.associate { (p, ra, dec) ->
        p to AstroGeoBasis.entries.associateWith { EquatorialPos(ra, dec) }
      }
    )

  private fun loc(latDeg: Double, lngDeg: Double) = LatLng(latDeg.toLat(), lngDeg.toLng())

  private val basis = AstroGeoBasis.IN_MUNDO

  // θ=100, α=50, δ=20 → λMC = normalizeSigned(50-100) = -50 , λIC = +130
  private val context = ctx(100.0, Triple(SUN, 50.0, 20.0))

  @Test
  fun testMcIcLongitude() {
    assertEquals(-50.0, context.longitudeAt(SUN, GeoAngle.MC, 0.0, basis)!!, 1e-9)
    assertEquals(130.0, context.longitudeAt(SUN, GeoAngle.IC, 0.0, basis)!!, 1e-9)
  }

  @Test
  fun testMcIndependentOfLatitude() {
    val l0 = context.longitudeAt(SUN, GeoAngle.MC, 0.0, basis)!!
    val l60 = context.longitudeAt(SUN, GeoAngle.MC, 60.0, basis)!!
    assertEquals(l0, l60, 1e-12)
  }

  /** ASC/DESC 反解出的經度，代回地平座標，該星高度必為 0 */
  @Test
  fun testAscDescRoundTripAltitudeZero() {
    val phi = 25.0
    for (angle in listOf(GeoAngle.ASC, GeoAngle.DESC)) {
      val lng = context.longitudeAt(SUN, angle, phi, basis)
      assertNotNull(lng, "$angle should exist at φ=$phi")
      // H = LST - α = θ + λ - α
      val hDeg = 100.0 + lng - 50.0
      val h = Math.toRadians(hDeg)
      val alt = sin(Math.toRadians(phi)) * sin(Math.toRadians(20.0)) +
        cos(Math.toRadians(phi)) * cos(Math.toRadians(20.0)) * cos(h)
      assertEquals(0.0, alt, 1e-9, "$angle altitude should be 0")
    }
  }

  /** ASC 在東（HA ∈ (-180,0)），DESC 在西（HA ∈ (0,180)） */
  @Test
  fun testAscEastDescWest() {
    val phi = 25.0
    val lngAsc = context.longitudeAt(SUN, GeoAngle.ASC, phi, basis)!!
    val lngDesc = context.longitudeAt(SUN, GeoAngle.DESC, phi, basis)!!
    val haAsc = (100.0 + lngAsc - 50.0).let { if (it > 180) it - 360 else if (it <= -180) it + 360 else it }
    val haDesc = (100.0 + lngDesc - 50.0).let { if (it > 180) it - 360 else if (it <= -180) it + 360 else it }
    assertTrue(haAsc < 0.0 && haAsc > -180.0, "ASC hour angle should be negative , got $haAsc")
    assertTrue(haDesc > 0.0 && haDesc < 180.0, "DESC hour angle should be positive , got $haDesc")
  }

  /** 極區截斷：|φ| > 90 - |δ| 時 AS/DS 不存在，MC/IC 仍在 */
  @Test
  fun testPolarTruncation() {
    // δ=20 → 截斷界線 φ=70
    assertNotNull(context.longitudeAt(SUN, GeoAngle.ASC, 69.0, basis))
    assertNull(context.longitudeAt(SUN, GeoAngle.ASC, 71.0, basis))
    assertNull(context.longitudeAt(SUN, GeoAngle.DESC, -71.0, basis))
    assertNotNull(context.longitudeAt(SUN, GeoAngle.MC, 71.0, basis))
    assertNotNull(context.longitudeAt(SUN, GeoAngle.IC, -89.0, basis))
  }

  /** 站在 MC 線上 → 到 MC 線的角距為 0 */
  @Test
  fun testDistanceOnMcLineIsZero() {
    val loc = loc(40.0, -50.0)
    assertEquals(0.0, context.angularityToLine(loc, SUN, GeoAngle.MC, basis)!!, 1e-9)
    assertEquals(0.0, context.kmToLine(loc, SUN, GeoAngle.MC, basis)!!, 1e-6)
  }

  /**
   * 4.2 分段式的回歸測試：站在 IC 線正上方的點，到 MC 線的距離是 (90-|φ|)，**不是 0**。
   * 不分段的 asin 閉合解會把整條大圓（= MC 線 ∪ IC 線）都當成 MC 線。
   */
  @Test
  fun testPointOnIcLineIsNotZeroToMcLine() {
    val phi = 40.0
    val loc = loc(phi, 130.0)   // λIC = 130 , 正好在 IC 線上
    assertEquals(0.0, context.angularityToLine(loc, SUN, GeoAngle.IC, basis)!!, 1e-9)
    val degToMc = context.angularityToLine(loc, SUN, GeoAngle.MC, basis)!!
    assertEquals(90.0 - phi, degToMc, 1e-9)
    assertEquals((90.0 - phi) * PI / 180 * 6371.0, context.kmToLine(loc, SUN, GeoAngle.MC, basis)!!, 1e-6)
  }

  /** 赤道上 Δλ=30 → 角距 30° ≈ 3336 km */
  @Test
  fun testDistanceAtEquator() {
    val loc = loc(0.0, -20.0)   // λMC = -50 , Δλ = 30
    assertEquals(30.0, context.angularityToLine(loc, SUN, GeoAngle.MC, basis)!!, 1e-9)
    assertEquals(30.0 * PI / 180 * 6371.0, context.kmToLine(loc, SUN, GeoAngle.MC, basis)!!, 1e-3)
  }

  /** 分段公式在 |Δλ| = 90° 處連續 */
  @Test
  fun testDistanceContinuityAtQuadrature() {
    val phi = 35.0
    val near = context.angularityToLine(loc(phi, -50.0 + 89.999), SUN, GeoAngle.MC, basis)!!
    val far = context.angularityToLine(loc(phi, -50.0 + 90.001), SUN, GeoAngle.MC, basis)!!
    assertEquals(90.0 - phi, near, 0.01)
    assertEquals(90.0 - phi, far, 0.01)
    assertTrue(abs(near - far) < 0.01)
  }

  /** context 內沒有的星體 → null，不丟例外 */
  @Test
  fun testUnknownPointReturnsNull() {
    assertNull(context.longitudeAt(MOON, GeoAngle.MC, 0.0, basis))
    assertNull(context.angularityToLine(loc(0.0, 0.0), MOON, GeoAngle.MC, basis))
    assertNull(context.kmToLine(loc(0.0, 0.0), MOON, GeoAngle.MC, basis))
  }

  // ========== ASC/DESC 取樣距離 ==========

  /** 站在 ASC 線上 → 到 ASC 線的角距 ≈ 0（取樣 + 細化路徑） */
  @Test
  fun testAscDistanceOnLineIsZero() {
    val phi = 25.0
    val lng = context.longitudeAt(SUN, GeoAngle.ASC, phi, basis)!!
    val d = context.angularityToLine(loc(phi, lng), SUN, GeoAngle.ASC, basis)!!
    assertEquals(0.0, d, 0.01)
  }

  /** kmToLine 與 angularityToLine 對 AS/DS 也維持同一換算 */
  @Test
  fun testAscKmConsistent() {
    val loc = loc(10.0, 30.0)
    val deg = context.angularityToLine(loc, SUN, GeoAngle.DESC, basis)!!
    val km = context.kmToLine(loc, SUN, GeoAngle.DESC, basis)!!
    assertEquals(deg * PI / 180 * 6371.0, km, 1e-6)
  }

  // ========== angularity（map 版） ==========

  @Test
  fun testAngularityMapMatchesSingleQueries() {
    val loc = loc(25.0, 121.5)
    val map = context.angularity(loc, basis)
    val sun = map.getValue(SUN)
    for (angle in GeoAngle.entries) {
      assertEquals(context.angularityToLine(loc, SUN, angle, basis)!!, sun.getValue(angle), 1e-9)
    }
  }

  // ========== line() ==========

  /** MC 線是垂直線：單一線段、經度恆定 */
  @Test
  fun testLineMcVertical() {
    val segments = context.line(SUN, GeoAngle.MC, basis)
    assertEquals(1, segments.size)
    assertTrue(segments[0].size > 2)
    segments[0].forEach { assertEquals(-50.0, it.lng.value, 1e-9) }
  }

  /**
   * 此組參數的 ASC 線經度範圍 (−180,−50)∪(130,180]，跨換日線：
   * 必須切段，且每段內無 |Δλ|>180 的跳躍、經度單調、緯度不超出截斷界線
   */
  @Test
  fun testLineAscSegmentsWellFormed() {
    val segments = context.line(SUN, GeoAngle.ASC, basis)
    assertTrue(segments.isNotEmpty())
    assertTrue(segments.size >= 2, "should split at dateline , got ${segments.size} segment(s)")
    for (seg in segments) {
      for (pt in seg) {
        assertTrue(pt.lat.value.absoluteValue <= 70.0 + 1e-9, "beyond polar truncation: ${pt.lat.value}")
      }
      seg.zipWithNext().forEach { (a, b) ->
        assertTrue((a.lng.value - b.lng.value).absoluteValue < 180.0, "dateline jump inside segment")
      }
      if (seg.size >= 3) {
        val diffs = seg.zipWithNext().map { (a, b) -> b.lng.value - a.lng.value }
        assertTrue(diffs.all { it >= -1e-9 } || diffs.all { it <= 1e-9 }, "λ not monotonic within segment")
      }
    }
  }

  // ========== localSpace ==========

  /** 上中天且 δ<φ → 正南 180°；東昇（H=−90, δ=0）→ 正東 90° */
  @Test
  fun testLocalSpaceSelfChecks() {
    // MOON: α=50, δ=0
    val ctx2 = ctx(100.0, Triple(SUN, 50.0, 20.0), Triple(MOON, 50.0, 0.0))
    // loc λ=−50 → H = θ+λ−α = 0 , δ=0 < φ=25 → 正南
    assertEquals(180.0, ctx2.localSpace(loc(25.0, -50.0), basis).getValue(MOON), 1e-9)
    // loc λ=−140 → H = −90 , δ=0 → 正東
    assertEquals(90.0, ctx2.localSpace(loc(25.0, -140.0), basis).getValue(MOON), 1e-9)
  }

  // ========== parans ==========

  /** 兩條 MC/IC 線（垂直平行線）永不相交 */
  @Test
  fun testParansMeridianOnlyIsEmpty() {
    val ctx2 = ctx(100.0, Triple(SUN, 50.0, 20.0), Triple(MOON, 80.0, 10.0))
    assertTrue(ctx2.parans(SUN, GeoAngle.MC, MOON, GeoAngle.IC, basis).isEmpty())
    assertTrue(ctx2.parans(SUN, GeoAngle.MC, MOON, GeoAngle.MC, basis).isEmpty())
  }

  /**
   * MC × ASC：s1 的 MC 線在 λ=−50（垂直），s2 (α=80, δ=10) 的 ASC 線與之交於
   * H₂ = 30° → tan φ = −cos30°/tan10° → φ ≈ −78.496°
   */
  @Test
  fun testParansMcAsc() {
    val ctx2 = ctx(100.0, Triple(SUN, 50.0, 20.0), Triple(MOON, 80.0, 10.0))
    val points = ctx2.parans(SUN, GeoAngle.MC, MOON, GeoAngle.ASC, basis)
    assertEquals(1, points.size)
    val pt = points[0]
    assertEquals(-78.496, pt.lat.value, 0.01)
    assertEquals(-50.0, pt.lng.value, 0.01)
    // 交點代回兩條線方程皆成立
    assertEquals(-50.0, ctx2.longitudeAt(SUN, GeoAngle.MC, pt.lat.value, basis)!!, 1e-9)
    assertEquals(-50.0, ctx2.longitudeAt(MOON, GeoAngle.ASC, pt.lat.value, basis)!!, 0.01)
  }

  /** ASC × ASC：解析預估根在 φ ≈ −15.2 附近，且每個交點須同時滿足兩線方程 */
  @Test
  fun testParansAscAscSelfConsistent() {
    val ctx2 = ctx(100.0, Triple(SUN, 50.0, 20.0), Triple(MOON, 60.0, -15.0))
    val points = ctx2.parans(SUN, GeoAngle.ASC, MOON, GeoAngle.ASC, basis)
    assertTrue(points.isNotEmpty())
    for (pt in points) {
      val l1 = ctx2.longitudeAt(SUN, GeoAngle.ASC, pt.lat.value, basis)!!
      val l2 = ctx2.longitudeAt(MOON, GeoAngle.ASC, pt.lat.value, basis)!!
      assertEquals(l1, l2, 0.01)
      assertEquals(l1, pt.lng.value, 0.01)
    }
  }
}
