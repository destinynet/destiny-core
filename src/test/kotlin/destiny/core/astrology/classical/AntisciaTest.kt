/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPosWithAzimuth
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.classical.rules.PatternTranslator
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.*

class AntisciaTest {

  @Test
  fun `映點與反映點的度數`() {
    assertEquals(110.0, 70.0.toZodiacDegree().antiscion.value, 1e-9)        // 雙子 10° ↔ 巨蟹 20°
    assertEquals(80.0, 100.0.toZodiacDegree().antiscion.value, 1e-9)        // 巨蟹 10° ↔ 雙子 20°
    assertEquals(150.0, 30.0.toZodiacDegree().antiscion.value, 1e-9)        // 金牛 0° ↔ 處女 0°
    assertEquals(330.0, 30.0.toZodiacDegree().contraAntiscion.value, 1e-9)  // 金牛 0° ↔ 雙魚 0°
    assertEquals(0.0, 0.0.toZodiacDegree().contraAntiscion.value, 1e-9)
  }

  /** 太陽 70°（映點 110°、反映點 290°）：金星 110.5° 映點（差 0.5°）、火星 290.2° 反映點（差 0.2°）、木星 112° 差 2° 不收 */
  private val h: IHoroscopeModel = run {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(SUN to 70.0, VENUS to 110.5, MARS to 290.2, JUPITER to 112.0)
      .mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    mockk { every { positionMap } returns posMap }
  }

  @Test
  fun `一對星一筆 容許度 反映點可關`() {
    val found = AntisciaImpl().getResults(h)
    assertEquals(setOf(Triple(SUN, VENUS, false), Triple(SUN, MARS, true)), found.map { Triple(it.planet, it.other, it.contra) }.toSet())
    assertEquals(0.5, found.single { !it.contra }.orb, 1e-9)

    val noContra = AntisciaImpl(AntisciaOptions(contra = false)).getResults(h)
    assertEquals(listOf(VENUS), noContra.map { it.other })
    assertEquals(3, AntisciaImpl(AntisciaOptions(orb = 2.5)).getResults(h).size, "放寬到 2.5° 收進木星")
  }

  @Test
  fun `判斷層與句子`() {
    val ctx = ClassicalPatternContext(
      RulerPtolemyImpl, mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      antisciaImpl = AntisciaImpl(),
    )
    assertTrue(ctx.antiscion in ctx.defaultFactories)
    val sun = ctx.antiscion.getPatterns(SUN, h).filterIsInstance<Misc.InAntiscia>()
    assertEquals(2, sun.size)
    assertTrue(ctx.antiscion.getPatterns(VENUS, h).isEmpty(), "掛在排前面的那顆，不重複")
    val zh = sun.map { PatternTranslator.getDescriptor(it).getDescription(Locale.TAIWAN) }
    assertTrue(zh.any { "太陽 與 金星 互為映點（相差 0.50°）" in it } && zh.any { "太陽 與 火星 互為反映點" in it }, zh.toString())
    val en = PatternTranslator.getDescriptor(sun.first { !it.antiscion.contra }).getDescription(Locale.ENGLISH)
    assertTrue("Sun and Venus are in antiscia (0.50° off)" in en, en)
  }
}
