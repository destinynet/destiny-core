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
import destiny.core.calendar.Location
import destiny.core.calendar.LocationPlace
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RelocationScorerTest {

  private val scorer = RelocationScorerClassicImpl()

  private val config = RelocationScoreConfig(
    angleWeights = mapOf(GeoAngle.MC to 1.0, GeoAngle.ASC to 0.8, GeoAngle.IC to 0.5, GeoAngle.DESC to 0.4),
    starWeights = mapOf(SUN to 1.0, JUPITER to 0.5),
    starValences = mapOf(SUN to 0.5, JUPITER to 1.0),
    orbDeg = 10.0
  )

  private val place = LocationPlace(Location(25.0.toLat(), 121.5.toLng()), "somewhere")

  private fun candidate(nearestLine: Map<AstroPoint, LineDistance>) =
    RelocationCandidate(place, emptyMap(), nearestLine, emptyList(), null)

  private fun line(angle: GeoAngle, deg: Double) = LineDistance(angle, deg, Math.toRadians(deg) * 6371.0)

  @Test
  fun testEmptyCandidateScoresZero() {
    val c = candidate(emptyMap())
    assertEquals(0.0, scorer.score(c, config).value)
    assertTrue(scorer.reasons(c, config).isEmpty())
  }

  /** 太陽正壓 MC 線（orb=0）且 MC 是最大角別權重 → 滿分 */
  @Test
  fun testExactOnStrongestLineScoresFull() {
    val c = candidate(mapOf<AstroPoint, LineDistance>(SUN to line(GeoAngle.MC, 0.0)))
    assertEquals(1.0, scorer.score(c, config).value, 1e-12)
  }

  /** orb 線性衰減：半 orb → 半分 */
  @Test
  fun testLinearOrbDecay() {
    val c = candidate(mapOf<AstroPoint, LineDistance>(SUN to line(GeoAngle.MC, 5.0)))
    assertEquals(0.5, scorer.score(c, config).value, 1e-12)
  }

  /** 超出 orb → 貢獻 0 */
  @Test
  fun testBeyondOrbNoContribution() {
    val c = candidate(mapOf<AstroPoint, LineDistance>(SUN to line(GeoAngle.MC, 12.0)))
    assertEquals(0.0, scorer.score(c, config).value)
    assertTrue(scorer.reasons(c, config).isEmpty())
  }

  /**
   * 多星加總與正規化：
   * SUN on MC (orb 0) 貢獻 1.0×1.0×1.0 = 1.0
   * JUPITER on ASC (orb 5) 貢獻 0.5×0.8×0.5 = 0.2
   * 分母 = (1.0 + 0.5) × maxAngleWeight(1.0) = 1.5 → score = 1.2/1.5 = 0.8
   */
  @Test
  fun testMultiPointNormalization() {
    val c = candidate(
      mapOf<AstroPoint, LineDistance>(
        SUN to line(GeoAngle.MC, 0.0),
        JUPITER to line(GeoAngle.ASC, 5.0)
      )
    )
    assertEquals(0.8, scorer.score(c, config).value, 1e-12)

    val reasons = scorer.reasons(c, config)
    assertEquals(2, reasons.size)
    // 依貢獻分降冪
    assertEquals(SUN, reasons[0].point)
    assertEquals(GeoAngle.MC, reasons[0].angle)
    assertEquals(1.0, reasons[0].contribution, 1e-12)
    assertEquals(JUPITER, reasons[1].point)
    assertEquals(GeoAngle.ASC, reasons[1].angle)
    assertEquals(5.0, reasons[1].orbDeg, 1e-12)
    assertEquals(0.2, reasons[1].contribution, 1e-12)
  }

  // ========== suitability（吉凶軸，0.5 = 中性） ==========

  /** 空 candidate → 中性 0.5 */
  @Test
  fun testSuitabilityNeutralWhenEmpty() {
    assertEquals(0.5, scorer.suitability(candidate(emptyMap()), config).value, 1e-12)
  }

  /** 吉星（JUPITER valence +1.0）正壓最強線 → suitability = (1×1+1)/2 = 1.0 */
  @Test
  fun testBeneficExactOnLineIsFullySuitable() {
    val c = candidate(mapOf<AstroPoint, LineDistance>(JUPITER to line(GeoAngle.MC, 0.0)))
    assertEquals(1.0, scorer.suitability(c, config).value, 1e-12)
  }

  /** 凶星（MARS valence −0.8）正壓最強線 → net = −0.8 → suitability = 0.1，低於中性 */
  @Test
  fun testMaleficExactOnLineIsUnsuitable() {
    val maleficConfig = config.copy(
      starWeights = mapOf(MARS to 1.0),
      starValences = mapOf(MARS to -0.8),
    )
    val c = candidate(mapOf<AstroPoint, LineDistance>(MARS to line(GeoAngle.MC, 0.0)))
    val s = scorer.suitability(c, maleficConfig)
    assertEquals(0.1, s.value, 1e-12)
    assertTrue(s.value < 0.5, "malefic line should score below neutral")
    // 但強度軸仍然是滿分 —— 「很有事」與「不適合」並存
    assertEquals(1.0, scorer.score(c, maleficConfig).value, 1e-12)
  }

  /**
   * 吉凶混合：SUN on MC orb0 (w1.0, v0.5) + JUPITER on ASC orb5 (w0.5, v1.0)
   * net = (1.0×1×1×0.5 + 0.5×0.8×0.5×1.0) / ((1.0+0.5)×1.0) = 0.7/1.5
   * suitability = (0.4667+1)/2 ≈ 0.7333
   */
  @Test
  fun testMixedSuitability() {
    val c = candidate(
      mapOf<AstroPoint, LineDistance>(
        SUN to line(GeoAngle.MC, 0.0),
        JUPITER to line(GeoAngle.ASC, 5.0)
      )
    )
    assertEquals((0.7 / 1.5 + 1.0) / 2.0, scorer.suitability(c, config).value, 1e-12)
  }

  /** reasons 帶出該星的 valence 因子，供 AI 詮釋層使用 */
  @Test
  fun testReasonsCarryValence() {
    val c = candidate(mapOf<AstroPoint, LineDistance>(SUN to line(GeoAngle.MC, 0.0)))
    val reason = scorer.reasons(c, config).single()
    assertEquals(0.5, reason.valence, 1e-12)
  }

  /** 權重表沒列的星體：不計分、也不進分母（否則會拖低所有分數） */
  @Test
  fun testUnweightedStarIgnored() {
    val c = candidate(
      mapOf<AstroPoint, LineDistance>(
        SUN to line(GeoAngle.MC, 0.0),
        MOON to line(GeoAngle.MC, 0.0)   // MOON 不在 starWeights
      )
    )
    assertEquals(1.0, scorer.score(c, config).value, 1e-12)
    assertEquals(1, scorer.reasons(c, config).size)
  }

  // ========== nearestLines helper ==========

  /** 從幾何層取每顆星最近的角線：θ=100, SUN(α=50, δ=20) → λIC = 130 */
  @Test
  fun testNearestLinesPicksClosestAngle() {
    val ctx = AstroGeoContext(
      GmtJulDay(2451545.0),
      100.0,
      mapOf<AstroPoint, Map<AstroGeoBasis, EquatorialPos>>(
        SUN to mapOf(AstroGeoBasis.IN_MUNDO to EquatorialPos(50.0, 20.0))
      )
    )
    // (40, 128)：距 IC 線（λ=130）僅約 1.53°，遠比 MC / AS / DS 近
    val nearest = ctx.nearestLines(LatLng(40.0.toLat(), 128.0.toLng()), AstroGeoBasis.IN_MUNDO)
    val sun = nearest.getValue(SUN)
    assertEquals(GeoAngle.IC, sun.angle)
    assertEquals(1.5325, sun.deg, 0.001)
    assertEquals(sun.deg * PI / 180 * 6371.0, sun.km, 1e-9)

    // 正壓 MC 線 → MC, 0°
    val onMc = ctx.nearestLines(LatLng(40.0.toLat(), (-50.0).toLng()), AstroGeoBasis.IN_MUNDO).getValue(SUN)
    assertEquals(GeoAngle.MC, onMc.angle)
    assertEquals(0.0, onMc.deg, 1e-9)
  }
}
