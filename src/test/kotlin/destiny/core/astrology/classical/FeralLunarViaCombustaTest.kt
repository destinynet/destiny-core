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

class FeralLunarViaCombustaTest {

  private fun model(lngs: Map<Planet, Double>, speeds: Map<Planet, Double> = emptyMap()): IHoroscopeModel {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = lngs.mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    return mockk {
      every { positionMap } returns posMap
      Planet.classicalList.forEach { p ->
        every { getStarPosition(p) } returns speeds[p]?.let { v -> mockk<IStarPositionWithAzimuth> { every { speedLng } returns v } }
      }
    }
  }

  // ======================== 野生 ========================

  /**
   * 火星 100°（巨蟹）；其餘的星都擠在 130°–160°（獅子到處女）。
   * 與火星的角距 30°–60° 之間：落在半六分與六分之間，六分（60°）要火星與那顆星的星芒半徑和夠大才算
   */
  private val feralLngs = mapOf(MARS to 100.0, SUN to 135.0, MOON to 140.0, MERCURY to 145.0, VENUS to 130.0, JUPITER to 150.0, SATURN to 158.0)

  @Test
  fun `野生 以星芒容許度計`() {
    val impl = FeralImpl()
    val h = model(feralLngs)
    // 火星與土星 58°：Al-Biruni 火 8、土 9，半徑和 8.5 ⇒ 六分成立
    assertNull(impl.getFeral(h, MARS), "與土星成六分")

    val lonely = model(feralLngs + (SATURN to 152.0) + (JUPITER to 148.0))
    // 最遠的是土星 52°：差 8°，半徑和 8.5 —— 仍在容許度內
    assertNull(impl.getFeral(lonely, MARS))

    val reallyLonely = model(feralLngs + (SATURN to 148.0) + (JUPITER to 146.0))
    assertEquals(Feral(MARS, FeralOptions.Basis.BY_ORB), impl.getFeral(reallyLonely, MARS), "所有星都在 30°–48°，沒有任何托勒密相位")
  }

  @Test
  fun `野生 以星座計`() {
    val impl = FeralImpl(FeralOptions.BY_SIGN)
    // 火星在巨蟹；其餘的星都在獅子（第 2 個星座）與處女（第 3 個 ⇒ 六分）
    assertNull(impl.getFeral(model(feralLngs), MARS), "處女是巨蟹起第 3 個星座（六分）")
    val allLeo = feralLngs.mapValues { (p, d) -> if (p == MARS) d else 125.0 + p.ordinalIndex() }
    assertEquals(Feral(MARS, FeralOptions.Basis.BY_SIGN), impl.getFeral(model(allLeo), MARS), "全部在獅子 ⇒ 不相見，以星座計沒有相位")
  }

  private fun Planet.ordinalIndex() = Planet.classicalList.indexOf(this).toDouble()

  // ======================== 月亮的離與合 ========================

  /**
   * 月亮 100°、每天 13°。土星 85°（不動）：月亮 15° 前與它合相 ⇒ 最後離開的；
   * 金星 225°（不動）：與月亮差 125°，月亮再走 5° 成三分 ⇒ 下一個入的。
   * 其餘的星：火星 20°（1.6 天前六分、0.8 天後四分）、木星 350°（1.55 天前四分、0.78 天後三分）、太陽 300°（1.67 天後對沖），都比土星、金星遠
   */
  private val lunarLngs = mapOf(MOON to 100.0, SATURN to 85.0, VENUS to 225.0, SUN to 300.0, MERCURY to 310.0, MARS to 20.0, JUPITER to 350.0)
  private val lunarSpeeds = mapOf(MOON to 13.0, SATURN to 0.0, VENUS to 0.0, SUN to 1.0, MERCURY to 1.0, MARS to 0.5, JUPITER to 0.1)

  @Test
  fun `月亮 最後離開與下一個入`() {
    val c = LunarConnectionImpl().getConnection(model(lunarLngs, lunarSpeeds))!!
    val sep = c.separating!!
    val app = c.applying!!
    assertEquals(SATURN, sep.planet)
    assertEquals(Aspect.CONJUNCTION, sep.aspect)
    assertEquals(15.0, sep.orb, 1e-9)
    assertEquals(15.0 / 13.0, sep.days, 1e-9)
    assertEquals(VENUS, app.planet)
    assertEquals(Aspect.TRINE, app.aspect)
    assertEquals(5.0, app.orb, 1e-9)
  }

  @Test
  fun `月亮 只看目前星座`() {
    // 月亮 100° 在巨蟹 10°：15° 前與土星合相時還在雙子 ⇒ 不算；5° 後與金星三分仍在巨蟹 ⇒ 算
    val c = LunarConnectionImpl(LunarConnectionOptions.WITHIN_SIGN).getConnection(model(lunarLngs, lunarSpeeds))!!
    assertNull(c.separating)
    assertEquals(VENUS, c.applying!!.planet)
  }

  @Test
  fun `月亮 速度不明時用平均日速`() {
    val c = LunarConnectionImpl().getConnection(model(lunarLngs))!!
    assertEquals(SATURN, c.separating!!.planet)
    assertEquals(VENUS, c.applying!!.planet)
  }

  // ======================== 燃燒路 ========================

  @Test
  fun `燃燒路 天秤 15 到天蠍 15`() {
    val impl = ViaCombustaImpl()
    assertEquals(ViaCombusta(MOON, 195.0.toZodiacDegree()), impl.getViaCombusta(model(mapOf(MOON to 195.0)), MOON), "天秤 15° 含")
    assertNull(impl.getViaCombusta(model(mapOf(MOON to 225.0)), MOON), "天蠍 15° 不含")
    assertNull(impl.getViaCombusta(model(mapOf(MARS to 200.0)), MARS), "預設只判月亮")
    assertNull(ViaCombustaImpl(ViaCombustaOptions.AL_BIRUNI).getViaCombusta(model(mapOf(MOON to 196.0)), MOON), "Al-Biruni 從天秤 19° 起")
  }

  // ======================== DTO ========================

  @Test
  fun `DTO 的 kind`() {
    val facts: List<ClassicalFact> = listOf(
      Phasis(MERCURY, Phasis.Type.STATION_RETROGRADE, 4.25, 231.0.toZodiacDegree(), true),
      Feral(MARS, FeralOptions.Basis.BY_ORB),
      LunarConnection(LunarConnection.Link(SATURN, Aspect.CONJUNCTION, 15.0, 1.25), LunarConnection.Link(VENUS, Aspect.TRINE, 5.0, 0.5)),
      ViaCombusta(MOON, 200.0.toZodiacDegree()),
    )
    val kinds = facts.map { f ->
      val dto = f.toChartDto()!!.withoutTrack()
      val json = Json.encodeToString(HoroscopeClassicalFactDto.serializer(), dto)
      assertEquals(dto, Json.decodeFromString(HoroscopeClassicalFactDto.serializer(), json), json)
      Json.parseToJsonElement(json).let { (it as kotlinx.serialization.json.JsonObject).getValue("kind").toString().trim('"') }
    }
    assertEquals(listOf("phasis", "feral", "lunar_connection", "via_combusta"), kinds)
    assertEquals(listOf("Planet.MOON", "Planet.SATURN", "Planet.VENUS"), facts[2].toChartDto()!!.members)
  }
}
