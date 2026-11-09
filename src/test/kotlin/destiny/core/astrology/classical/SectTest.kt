/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlin.test.*

class SectTest {

  /**
   * 上升點 0°（牡羊 0°）：黃經 0–180° 在地平線下、180–360° 在地平線上。夜盤，太陽 100°（地平下）。
   * - 月亮 200° 天秤（陽、地平上）：同隊且在上，但月亮要陰性星座 ⇒ 不得時
   * - 金星 220° 天蠍（陰、地平上）：兩種判法都得時
   * - 火星 250° 射手（陽、地平上）：同隊在上；依星的陰陽（火星陽）得時，依晝夜歸屬（夜星要陰）不得時
   * - 木星 30° 金牛（陰、地平下）：異隊在下，但要陽性星座 ⇒ 不得時
   * - 土星 60° 雙子（陽、地平下）：異隊在下、陽性 ⇒ ḥalb 判法得時；舊判法只收同隊 ⇒ 不得時
   * - 水星 80° 雙子：黃經在太陽之前 ⇒ 東出、晝星（陽）；異隊在下、陽性 ⇒ 得時（舊判法不判水星）
   */
  private fun model(mercury: Double = 80.0): IHoroscopeModel {
    val lngs: Map<AstroPoint, Double> = mapOf(
      Axis.RISING to 0.0, SUN to 100.0, MOON to 200.0, MERCURY to mercury, VENUS to 220.0, MARS to 250.0, JUPITER to 30.0, SATURN to 60.0
    )
    val posMap: Map<AstroPoint, IPosWithAzimuth> = lngs.mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    return mockk { every { positionMap } returns posMap }
  }

  @Test
  fun `區分：兩隊、區分光體、同區分吉星與異區分凶星`() {
    val sect = SectImpl().getSect(model(), DayNight.NIGHT)!!
    assertEquals(MOON, sect.light)
    assertEquals(listOf(MOON, VENUS, MARS), sect.inSect)
    assertEquals(listOf(SUN, MERCURY, JUPITER, SATURN), sect.contrary, "水星東出歸晝隊")
    assertEquals(VENUS, sect.beneficOfSect)
    assertEquals(SATURN, sect.maleficContrary)
    assertEquals(DayNight.DAY, sect.mercurySect)

    val day = SectImpl().getSect(model(), DayNight.DAY)!!
    assertEquals(SUN, day.light)
    assertEquals(JUPITER, day.beneficOfSect)
    assertEquals(MARS, day.maleficContrary)
    assertEquals(listOf(SUN, MERCURY, JUPITER, SATURN), day.inSect)
  }

  @Test
  fun `水星西入歸夜隊`() {
    val sect = SectImpl().getSect(model(mercury = 110.0), DayNight.NIGHT)!!
    assertEquals(DayNight.NIGHT, sect.mercurySect)
    assertTrue(MERCURY in sect.inSect)
  }

  @Test
  fun `得時：Abu Ma'shar（ḥalb＋星的陰陽）`() {
    val results = SectImpl(HayzOptions.ABU_MASHAR).getHayzResults(model(), DayNight.NIGHT)
    assertEquals(listOf(MERCURY, VENUS, MARS, SATURN), results.map { it.planet })
    val saturn = results.single { it.planet == SATURN }
    assertFalse(saturn.aboveHorizon, "異隊的星在地平下才合宜")
    assertEquals(DayNight.DAY, saturn.planetSect)
    assertTrue(saturn.wantMasculine && saturn.masculineSign)
    val mars = results.single { it.planet == MARS }
    assertTrue(mars.aboveHorizon)
    assertTrue(mars.wantMasculine, "火星是夜星，但星本身是陽性")
  }

  @Test
  fun `得時：舊判法（同隊、地平上、依晝夜歸屬的陰陽）`() {
    val results = SectImpl(HayzOptions.IN_SECT_ABOVE).getHayzResults(model(), DayNight.NIGHT)
    assertEquals(listOf(VENUS), results.map { it.planet })
  }

  @Test
  fun `地平線以上升點黃經判，不看宮位`() {
    // 土星 60° 在地平下；換成日盤後土星同隊，ḥalb 要它在地平上 ⇒ 不得時
    assertNull(SectImpl().getHayz(model(), DayNight.DAY, SATURN))
  }

  /**
   * 不得時＝三項都相反（異隊、在地平上、星座陰陽不合）。同一張盤改成日盤：夜星是月、金、火（水星東出歸晝隊），三顆都在地平上。
   * - 月亮 200° 天秤（陽）：月亮要陰性 ⇒ 兩種判法都不得時
   * - 金星 220° 天蠍（陰）：陰陽合宜 ⇒ 不算
   * - 火星 250° 射手（陽）：依星的陰陽（火星陽）合宜 ⇒ 不算；依晝夜歸屬（夜星要陰）⇒ 不得時
   */
  @Test
  fun `不得時：與得時同一份判準`() {
    val h = model()
    fun outOfSect(options: HayzOptions) = Planet.classicalList.mapNotNull { SectImpl(options).getOutOfSect(h, DayNight.DAY, it) }.map { it.planet }
    assertEquals(listOf(MOON), outOfSect(HayzOptions.ABU_MASHAR))
    assertEquals(listOf(MOON, MARS), outOfSect(HayzOptions.IN_SECT_ABOVE))
    // 同隊的星永遠不算不得時；地平下的異隊星也不算（夜盤：土星 60° 異隊、在地平下）
    assertNull(SectImpl().getOutOfSect(h, DayNight.NIGHT, SATURN))
    assertNull(SectImpl().getOutOfSect(h, DayNight.NIGHT, VENUS))
  }

  @Test
  fun `同一顆星不會既得時又不得時`() {
    val h = model()
    for (options in listOf(HayzOptions.ABU_MASHAR, HayzOptions.IN_SECT_ABOVE)) for (sect in DayNight.entries) for (p in Planet.classicalList) {
      val impl = SectImpl(options)
      assertFalse(impl.getHayz(h, sect, p) != null && impl.getOutOfSect(h, sect, p) != null, "$options $sect $p")
    }
  }

  @Test
  fun `DTO 序列化帶 kind`() {
    val h = model()
    val facts: List<ClassicalFact> = listOf(SectImpl().getSect(h, DayNight.NIGHT)!!) + SectImpl().getHayzResults(h, DayNight.NIGHT)
    val json = Json.encodeToString(facts.map { it.toChartDto() })
    assertTrue(json.contains("\"kind\":\"sect\""))
    assertTrue(json.contains("\"kind\":\"hayz\""))
    assertTrue(json.contains("\"beneficOfSect\":\"Planet.VENUS\""))
  }
}
