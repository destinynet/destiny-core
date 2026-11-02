/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPosWithAzimuth
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.ClassicalInput
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.classical.rules.PatternTranslator
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.*

class DoryphoriaTest {

  /**
   * 太陽 30°：水星 5°（前方 25°，收）、金星 343°（前方 47°，超過 30°）、火星 45°（在後方，太陽只取前方）。
   * 月亮 132°：木星 150°（後方 18°，收）、土星 120°（在前方，月亮只取後方）
   */
  private val h: IHoroscopeModel = run {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(
      SUN to 30.0, MERCURY to 5.0, VENUS to 343.0, MARS to 45.0, MOON to 132.0, JUPITER to 150.0, SATURN to 120.0,
    ).mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    mockk { every { positionMap } returns posMap }
  }

  @Test
  fun `Ptolemy 太陽取前方 月亮取後方 一個星座以內`() {
    val impl = DoryphoriaPtolemyImpl()
    val sun = impl.getBearers(h, SUN, DayNight.NIGHT).single()
    assertEquals(MERCURY, sun.bearer)
    assertTrue(sun.preceding)
    assertEquals(25.0, sun.distance, 1e-9)
    assertNull(sun.inSect, "水星兩可")

    val moon = impl.getBearers(h, MOON, DayNight.NIGHT).single()
    assertEquals(JUPITER, moon.bearer)
    assertFalse(moon.preceding)
    assertEquals(false, moon.inSect, "木星是晝星、夜盤")
  }

  @Test
  fun `選項 只收同區分 月亮改取前方`() {
    assertTrue(DoryphoriaPtolemyImpl(DoryphoriaOptions(sameSectOnly = true)).getResults(h, DayNight.NIGHT).isEmpty())
    val moonPreceding = DoryphoriaPtolemyImpl(DoryphoriaOptions(moonPreceding = true)).getBearers(h, MOON, DayNight.NIGHT)
    assertEquals(listOf(SATURN), moonPreceding.map { it.bearer })
  }

  @Test
  fun `判斷層與句子`() {
    val ctx = ClassicalPatternContext(
      RulerPtolemyImpl, mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      doryphoriaImpl = DoryphoriaPtolemyImpl(),
    )
    assertTrue(ctx.spearBearing in ctx.defaultFactories)
    val input = ClassicalInput(h, DayNight.NIGHT)
    val moon = ctx.spearBearing.getPatterns(MOON, input).filterIsInstance<Misc.SpearBearing>().single()
    val zh = PatternTranslator.getDescriptor(moon).getDescription(Locale.TAIWAN)
    assertTrue("月亮 有持矛者" in zh && "木星（在月亮之後升起，相距 18.00°，與夜盤異區分）" in zh, zh)
    val en = PatternTranslator.getDescriptor(moon).getDescription(Locale.ENGLISH)
    assertTrue("Jupiter (rising after the Moon, 18.00° away; contrary to the sect of this night chart)" in en, en)
    assertTrue(ctx.spearBearing.getPatterns(MARS, input).isEmpty(), "只有光體")
  }
}
