/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlin.test.*

class CombustionTest {

  /**
   * 太陽 100°。月亮 100.2°（日心，12 角分）、水星 108.5°（正好 8.5°，灼傷的外緣）、金星 117°（正好 17°，光束下的外緣）、
   * 火星 105°（灼傷 5°）、木星 117.5°（17.5°，離得夠遠）、土星 90°（灼傷 10° 之外、光束下）。
   * 速度：月亮在太陽黃經較大的一側、又跑得比太陽快 ⇒ 距離拉大、正在遠離；火星也在較大的一側但比太陽慢 ⇒ 太陽追上來、正在靠近。
   * 日心的界線（17′）不測「正好等於」：17/60 在浮點數裡表示不精確，正好落在界線上的情形測不準
   */
  private fun model(speeds: Map<Planet, Double> = emptyMap()): IHoroscopeModel {
    val lngs = mapOf(SUN to 100.0, MOON to 100.2, MERCURY to 108.5, VENUS to 117.0, MARS to 105.0, JUPITER to 117.5, SATURN to 90.0)
    val posMap: Map<AstroPoint, IPosWithAzimuth> = lngs.mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    return mockk {
      every { positionMap } returns posMap
      lngs.keys.forEach { p ->
        every { getStarPosition(p) } returns speeds[p]?.let { v -> mockk<IStarPositionWithAzimuth> { every { speedLng } returns v } }
      }
    }
  }

  @Test
  fun `三圈的邊界`() {
    val impl = CombustionImpl()
    val h = model()
    assertEquals(Combustion.Zone.CAZIMI, impl.getCombustion(h, MOON)?.zone)
    assertEquals(Combustion.Zone.COMBUST, impl.getCombustion(h, MERCURY)?.zone, "8.5° 含在灼傷內")
    assertEquals(Combustion.Zone.COMBUST, impl.getCombustion(h, MARS)?.zone)
    assertEquals(Combustion.Zone.UNDER_BEAMS, impl.getCombustion(h, VENUS)?.zone, "17° 含在光束下內")
    assertNull(impl.getCombustion(h, JUPITER), "17.5° 已經離開光束")
    assertEquals(Combustion.Zone.UNDER_BEAMS, impl.getCombustion(h, SATURN)?.zone)
    assertNull(impl.getCombustion(h, SUN))
    assertEquals(listOf(MOON, MARS, MERCURY, SATURN, VENUS), impl.getResults(h).map { it.planet }, "由近到遠")
    assertEquals(5.0, impl.getCombustion(h, MARS)!!.distance, 1e-9)
    assertEquals(CombustionOptions.LILLY.limits, impl.getCombustion(h, MARS)!!.limits, "帶著判定用的界線")
  }

  @Test
  fun `正在靠近或遠離 速度不明為 null`() {
    val impl = CombustionImpl()
    assertNull(impl.getCombustion(model(), MARS)!!.approaching)
    val h = model(mapOf(SUN to 1.0, MOON to 13.0, MARS to 0.5))
    assertEquals(false, impl.getCombustion(h, MOON)!!.approaching, "月亮在黃經較大的一側、跑得比太陽快 ⇒ 遠離")
    assertEquals(true, impl.getCombustion(h, MARS)!!.approaching, "火星在黃經較大的一側、比太陽慢 ⇒ 太陽追上來")
  }

  @Test
  fun `換一組門檻`() {
    val wide = CombustionImpl(CombustionOptions(combust = 10.0, underBeams = 18.0))
    assertEquals(Combustion.Zone.COMBUST, wide.getCombustion(model(), SATURN)?.zone, "10° 在放寬後的灼傷內")
    assertEquals(Combustion.Zone.UNDER_BEAMS, wide.getCombustion(model(), JUPITER)?.zone, "17.5° 在放寬後的光束下內")
  }

  @Test
  fun `DTO 的 kind 是 combustion`() {
    val dto = Combustion(VENUS, Combustion.Zone.COMBUST, 3.98, true, CombustionOptions.LILLY.limits).toChartDto()!!
    assertEquals(HoroscopeCombustionDto(Planet.VENUS, Combustion.Zone.COMBUST, 3.98, true, 17.0 / 60.0, 8.5, 17.0), dto)
    assertEquals(listOf(Planet.VENUS, Planet.SUN), dto.members)
    val json = Json.encodeToString(HoroscopeClassicalFactDto.serializer(), dto)
    assertTrue(""""kind":"combustion"""" in json && """"zone":"COMBUST"""" in json, json)
    // 數字照 DTO 慣例捨去到兩位小數（DoubleTwoDecimalSerializer）：日心的 17′ ＝ 0.2833… 在 JSON 裡是 0.28
    assertEquals((dto as HoroscopeCombustionDto).copy(cazimiLimit = 0.28), Json.decodeFromString(HoroscopeClassicalFactDto.serializer(), json))
  }
}
