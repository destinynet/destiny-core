/**
 * Created by smallufo on 2026-11-20.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.EquatorialPos
import destiny.core.astrology.Planet.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.LatLng
import destiny.core.calendar.Lng.Companion.toLng
import kotlinx.serialization.json.Json
import kotlin.test.*

class AcgMapTest {

  private fun ctx(gmstDeg: Double, vararg entries: Triple<AstroPoint, Double, Double>): AstroGeoContext =
    AstroGeoContext(
      GmtJulDay(2451545.0),
      gmstDeg,
      entries.associate { (p, ra, dec) -> p to AstroGeoBasis.entries.associateWith { EquatorialPos(ra, dec) } }
    )

  private val basis = AstroGeoBasis.IN_MUNDO

  // 北半球夏季的太陽（δ=+20）＋南赤緯的火星
  private val summer = ctx(100.0, Triple(SUN, 50.0, 20.0), Triple(MARS, 200.0, -15.0))

  @Test
  fun testZenithIsOverhead() {
    for (p in listOf(SUN, MARS)) {
      val z = summer.zenith(p, basis)!!
      assertEquals(90.0, summer.altitude(z, p, basis)!!, 1e-6, "$p zenith altitude")
    }
    // lat = δ、lng = MC 線
    val z = summer.zenith(SUN, basis)!!
    assertEquals(20.0, z.lat.value, 1e-12)
    assertEquals(summer.longitudeAt(SUN, GeoAngle.MC, 0.0, basis)!!, z.lng.value, 1e-12)
  }

  @Test
  fun testZenithMissingStar() {
    assertNull(summer.zenith(VENUS, basis))
    assertNull(summer.altitude(LatLng(0.0.toLat(), 0.0.toLng()), VENUS, basis))
  }

  /** 晨昏線上每一點的太陽高度都是 0 */
  @Test
  fun testTerminatorAltitudeZero() {
    val t = summer.terminator(lngStep = 5.0)!!
    assertEquals(73, t.curve.size)   // -180..180 每 5 度
    assertEquals(-180.0, t.curve.first().lng.value, 1e-12)
    assertEquals(180.0, t.curve.last().lng.value, 1e-9)
    t.curve.forEach { assertEquals(0.0, summer.altitude(it, SUN, basis)!!, 1e-6, "at $it") }
  }

  /** 夜半球在晨昏線與 nightPole 之間：往 nightPole 方向移一點，太陽在地平線下；反方向在地平線上 */
  @Test
  fun testTerminatorNightSide() {
    val t = summer.terminator(lngStep = 10.0)!!
    assertEquals(-90.0, t.nightPole)
    t.curve.filter { it.lat.value.let { lat -> lat > -88 && lat < 88 } }.forEach { p ->
      val towardNight = LatLng((p.lat.value - 1.0).toLat(), p.lng)
      val towardDay = LatLng((p.lat.value + 1.0).toLat(), p.lng)
      assertTrue(summer.altitude(towardNight, SUN, basis)!! < 0, "night side at $p")
      assertTrue(summer.altitude(towardDay, SUN, basis)!! > 0, "day side at $p")
    }
    assertEquals(90.0, summer.altitude(t.subsolar, SUN, basis)!!, 1e-6)
  }

  @Test
  fun testTerminatorSouthernSummer() {
    val winter = ctx(100.0, Triple(SUN, 270.0, -23.4))
    assertEquals(90.0, winter.terminator()!!.nightPole)
  }

  /** 春秋分（δ=0）：不能除以 0，曲線仍在 ±90 內、高度仍近 0 */
  @Test
  fun testTerminatorEquinox() {
    val equinox = ctx(100.0, Triple(SUN, 0.0, 0.0))
    val t = equinox.terminator(lngStep = 7.0)!!
    t.curve.forEach {
      assertTrue(it.lat.value in -90.0..90.0)
      assertEquals(0.0, equinox.altitude(it, SUN, basis)!!, 1e-3)
    }
  }

  @Test
  fun testTerminatorWithoutSun() {
    assertNull(ctx(100.0, Triple(MARS, 0.0, 0.0)).terminator())
  }

  @Test
  fun testDto() {
    val dto = summer.toAcgMapDto(withParans = true)
    assertEquals(setOf<AstroPoint>(SUN, MARS), dto.lines.map { it.point }.toSet())
    assertEquals(8, dto.lines.size)   // 兩星 × 四角，δ ≤ 20 的 AS/DS 線都存在
    assertEquals(2, dto.zeniths.size)
    assertNotNull(dto.night)
    assertEquals(-90, dto.night.nightPole)
    assertTrue(dto.parans.isNotEmpty())
    dto.parans.forEach { assertTrue(it.point1 != it.point2) }

    val json = Json.encodeToString(AcgMapDto.serializer(), dto)
    val back = Json.decodeFromString(AcgMapDto.serializer(), json)
    assertEquals(dto.lines.size, back.lines.size)
    assertEquals(dto.zeniths.map { it.point }, back.zeniths.map { it.point })
    assertEquals(dto.night.curve.size, back.night!!.curve.size)
  }

  @Test
  fun testDtoWithoutParansByDefault() {
    assertTrue(summer.toAcgMapDto().parans.isEmpty())
  }
}
