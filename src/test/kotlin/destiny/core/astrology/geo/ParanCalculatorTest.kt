/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Aspect
import destiny.core.astrology.EquatorialPos
import destiny.core.astrology.Planet.MARS
import destiny.core.astrology.Planet.MOON
import destiny.core.astrology.Planet.SUN
import destiny.core.calendar.GmtJulDay
import destiny.tools.CircleTools.normalizeSigned
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.tan
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParanCalculatorTest {

  private val calc = ParanCalculator()

  private fun ctx(gmstDeg: Double, vararg entries: Triple<AstroPoint, Double, Double>): AstroGeoContext =
    AstroGeoContext(
      GmtJulDay(2451545.0),
      gmstDeg,
      entries.associate { (p, ra, dec) -> p to AstroGeoBasis.entries.associateWith { EquatorialPos(ra, dec) } }
    )

  private val two = ctx(100.0, Triple(SUN, 50.0, 20.0), Triple(MOON, 80.0, 10.0))

  private fun ParanQuery.of(anchor: AstroPoint, partner: AstroPoint) = copy(anchors = setOf(anchor), partners = setOf(partner))
  private val q = ParanQuery(emptySet(), emptySet())

  /** 兩顆都在 MC／IC：與緯度無關，不成線 */
  @Test
  fun meridianPairsAreEmpty() {
    val parans = calc.parans(two, q.of(SUN, MOON))
    assertTrue(parans.none { it.anchorAngle in setOf(GeoAngle.MC, GeoAngle.IC) && it.partnerAngle in setOf(GeoAngle.MC, GeoAngle.IC) })
  }

  /**
   * ☉ MC × ☽ ASC：☉ (α=50) 上中天時，☽ (α=80, δ=10) 的時角 = −30 → 半日弧 30°
   * → tan φ = −cos30° / tan10° → φ ≈ −78.496°
   */
  @Test
  fun mcAscClosedForm() {
    val lats = calc.parans(two, q.of(SUN, MOON)).filter { it.anchorAngle == GeoAngle.MC && it.partnerAngle == GeoAngle.ASC }.map { it.lat }
    assertEquals(1, lats.size)
    assertEquals(-78.496, lats.single(), 0.01)
    // 與 ACG 圖上兩條角線的交點同一個緯度
    assertEquals(two.longitudeAt(SUN, GeoAngle.MC, lats.single(), AstroGeoBasis.IN_MUNDO)!!,
      two.longitudeAt(MOON, GeoAngle.ASC, lats.single(), AstroGeoBasis.IN_MUNDO)!!, 1e-6)
  }

  /** 每一條 paran 緯線都通過 ACG 圖上兩條角線的交點（隨機盤面，合衝刑） */
  @Test
  fun everyParanIsALineCrossing() {
    val rnd = Random(1121)
    repeat(200) {
      val stars = listOf<AstroPoint>(SUN, MOON, MARS)
      val c = AstroGeoContext(GmtJulDay(2451545.0), rnd.nextDouble(0.0, 360.0), stars.associateWith {
        val pos = EquatorialPos(rnd.nextDouble(0.0, 360.0), rnd.nextDouble(-60.0, 60.0))
        AstroGeoBasis.entries.associateWith { pos }
      })
      calc.parans(c, ParanQuery(stars.toSet(), stars.toSet())).forEach { p ->
        val l1 = c.longitudeAt(p.anchor, p.anchorAngle, p.lat, AstroGeoBasis.IN_MUNDO)!!
        val l2 = c.longitudeAt(p.partner, p.partnerAngle!!, p.lat, AstroGeoBasis.IN_MUNDO)!!
        assertEquals(0.0, (l1 - l2).normalizeSigned(), 1e-6, "$p")
      }
    }
  }

  /** 合衝刑每對星只算一次（以先出現的 anchor 為準），16 種角別組合都由 anchor 那一側涵蓋 */
  @Test
  fun angularPairsAreDeduplicated() {
    val parans = calc.parans(two, ParanQuery(setOf(SUN, MOON), setOf(SUN, MOON)))
    assertTrue(parans.isNotEmpty())
    assertTrue(parans.all { it.anchor == SUN && it.partner == MOON })
  }

  /** 角別過濾：只要 anchor 在 MC */
  @Test
  fun anglesFilter() {
    val parans = calc.parans(two, q.of(SUN, MOON).copy(angles = setOf(GeoAngle.MC)))
    assertTrue(parans.isNotEmpty())
    assertTrue(parans.all { it.anchorAngle == GeoAngle.MC })
  }

  /** 世俗六合：anchor 上中天那一刻，partner 的世俗位置在 ±60°；partnerAngle 為 null */
  @Test
  fun mundaneSextile() {
    val parans = calc.parans(two, q.of(SUN, MOON).copy(aspects = setOf(Aspect.SEXTILE), angles = setOf(GeoAngle.MC)))
    assertTrue(parans.isNotEmpty())
    parans.forEach { p ->
      assertEquals(null, p.partnerAngle)
      // ☉ 在 MC：☽ 的時角 = α☉ − α☽ = −30（東側）；世俗位置 = 270 + 90·(H + D)/D
      val d = Math.toDegrees(acos(-tan(Math.toRadians(p.lat)) * tan(Math.toRadians(10.0))))
      val m = ParanCalculator.mundanePosition(-30.0, d)
      assertEquals(60.0, (m - 0.0).normalizeSigned().absoluteValue, 1e-6, "$p")
    }
  }

  @Test
  fun mundanePositionAtAngles() {
    assertEquals(0.0, ParanCalculator.mundanePosition(0.0, 100.0), 1e-9)
    assertEquals(90.0, ParanCalculator.mundanePosition(100.0, 100.0), 1e-9)
    assertEquals(180.0, ParanCalculator.mundanePosition(180.0, 100.0), 1e-9)
    assertEquals(270.0, ParanCalculator.mundanePosition(-100.0, 100.0), 1e-9)
    // 半夜弧 80°：IC 與 DSC 的正中間
    assertEquals(135.0, ParanCalculator.mundanePosition(140.0, 100.0), 1e-9)
  }

  /** context 裡沒有的星、與自己配對：略過 */
  @Test
  fun missingAndSelfAreSkipped() {
    assertTrue(calc.parans(two, q.of(SUN, MARS)).isEmpty())
    assertTrue(calc.parans(two, q.of(SUN, SUN)).isEmpty())
  }

  /** 月亮每天 +13°（赤經），赤緯固定 15°；太陽不動 */
  private fun moving(gmst: Double): AstroGeoContext {
    val base = ctx(gmst, Triple(SUN, 50.0, 20.0), Triple(MOON, 80.0, 15.0))
    val start = base.gmtJulDay.value - 0.6
    val samples = (0..30).map { i -> EquatorialPos((80.0 + 13.0 * (i / 24.0 - 0.6)), 15.0) }
    return base.copy(tracks = mapOf(MOON to EquatorialTrack(start, 1.0 / 24, AstroGeoBasis.entries.associateWith { samples })))
  }

  /** 沒有移動星參與：原樣回傳、沒有曲線 */
  @Test
  fun refineWithoutTracksIsIdentity() {
    calc.parans(two, q.of(SUN, MOON)).forEach { p ->
      assertEquals(p, calc.refine(two, p, AstroGeoBasis.IN_MUNDO, 120.0))
      assertEquals(null, calc.curve(two, p, AstroGeoBasis.IN_MUNDO))
    }
  }

  /**
   * 月亮在動：精算後的緯度上，太陽在這條經線到角的那一刻（離出生最近的一次），月亮（取那一刻的位置）也正好在它的角上。
   * 不同經度的解不同（不是水平線）。
   */
  @Test
  fun refineMakesBothAngularAtThatMoment() {
    val gmst = 100.0
    val c = moving(gmst)
    val parans = calc.parans(c, q.of(SUN, MOON))
    var moved = 0
    for (lng in listOf(-150.0, -60.0, 0.0, 45.0, 170.0)) {
      parans.forEach { p ->
        val r = calc.refine(c, p, AstroGeoBasis.IN_MUNDO, lng) ?: return@forEach
        if ((r.lat - p.lat).absoluteValue > 0.01) moved++
        // 太陽不動：到角時刻直接算
        val ha = when (p.anchorAngle) {
          GeoAngle.MC -> 0.0; GeoAngle.IC -> 180.0
          GeoAngle.ASC -> -Math.toDegrees(acos(-tan(Math.toRadians(r.lat)) * tan(Math.toRadians(20.0))))
          GeoAngle.DESC -> Math.toDegrees(acos(-tan(Math.toRadians(r.lat)) * tan(Math.toRadians(20.0))))
        }
        val dt = (50.0 + ha - gmst - lng).normalizeSigned() / 360.98564736629
        val moon = c.tracks.getValue(MOON).at(c.gmtJulDay.value + dt, AstroGeoBasis.IN_MUNDO)!!
        val at = AstroGeoContext(c.gmtJulDay, gmst + 360.98564736629 * dt, mapOf(
          SUN to AstroGeoBasis.entries.associateWith { EquatorialPos(50.0, 20.0) },
          MOON to AstroGeoBasis.entries.associateWith { moon },
        ))
        assertEquals(0.0, (at.longitudeAt(SUN, p.anchorAngle, r.lat, AstroGeoBasis.IN_MUNDO)!! - lng).normalizeSigned(), 1e-6, "$p @ $lng")
        assertEquals(0.0, (at.longitudeAt(MOON, p.partnerAngle!!, r.lat, AstroGeoBasis.IN_MUNDO)!! - lng).normalizeSigned(), 1e-5, "$p @ $lng")
      }
    }
    assertTrue(moved > 0)
  }

  /** 曲線：每個格點就是那個經度的精算解（同一支） */
  @Test
  fun curveMatchesRefine() {
    val c = moving(100.0)
    val p = calc.parans(c, q.of(SUN, MOON)).first { it.partnerAngle in setOf(GeoAngle.ASC, GeoAngle.DESC) }
    val curve = calc.curve(c, p, AstroGeoBasis.IN_MUNDO)!!
    assertEquals(Paran.CURVE_LNGS.size, curve.size)
    assertTrue(curve.count { it != null } > curve.size / 2)
    assertTrue(curve.filterNotNull().let { it.max() - it.min() } > 0.1, "隨經度變化")
  }
}
