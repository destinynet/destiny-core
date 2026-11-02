/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Axis
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPosWithAzimuth
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.classical.rules.PatternRequirement
import destiny.core.astrology.classical.rules.PatternTranslator
import destiny.core.astrology.classical.rules.requirements
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.*

class AversionTest {

  private fun deg(signIndex: Int, d: Double) = (signIndex * 30 + d).toZodiacDegree()

  private val ctx = ClassicalPatternContext(
    RulerPtolemyImpl, mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
    mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
    mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
  )

  /**
   * 上升天蠍（命主火星）。太陽金牛、主人金星在牡羊：太陽在金星起算的第 2 個星座 ⇒ 不相見；
   * 木星獅子、主人太陽在金牛：第 4 個（四分）⇒ 看得見；土星射手、主人木星在獅子：第 5 個（三分）；金星牡羊、主人火星在雙子：第 11 個（六分）；
   * 月亮巨蟹、水星雙子在自己的星座，沒有主人可言；火星雙子與水星同星座。命主火星在上升起算的第 8 個 ⇒ 不相見
   */
  @Test
  fun `只收與主人不相見 命主星與上升不相見`() {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(
      SUN to deg(1, 10.0), MOON to deg(3, 10.0), MERCURY to deg(2, 5.0), VENUS to deg(0, 20.0),
      MARS to deg(2, 15.0), JUPITER to deg(4, 10.0), SATURN to deg(8, 10.0),
    ).mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d } }
    val h = mockk<IHoroscopeModel> {
      every { positionMap } returns posMap
      every { cuspDegreeMap } returns mapOf(1 to deg(7, 3.0))
    }

    val found = ctx.aversions(h).toSet()
    assertEquals(
      setOf(
        Aversion(SUN, VENUS, 2, Aversion.Basis.RULER),
        Aversion(MARS, Axis.RISING, 8, Aversion.Basis.ASCENDANT),
      ),
      found
    )
    assertEquals(found, ctx.classicalFacts(h).filterIsInstance<Aversion>().toSet())
    assertTrue(ctx.averse in ctx.defaultFactories)

    val sun = ctx.averse.getPatterns(SUN, h).filterIsInstance<Misc.Aversion>().single()
    assertTrue(sun.requirements.isEmpty())
    val zh = PatternTranslator.getDescriptor(sun).getDescription(Locale.TAIWAN)
    assertTrue("金星 是 太陽 所在星座的主人" in zh && "第 2 個星座" in zh, zh)
    assertTrue("2nd sign" in PatternTranslator.getDescriptor(sun).getDescription(Locale.ENGLISH))

    val mars = ctx.averse.getPatterns(MARS, h).filterIsInstance<Misc.Aversion>().single()
    assertEquals(setOf(PatternRequirement.AXIS), mars.requirements)
    assertTrue("命主星 火星 與上升不相見" in PatternTranslator.getDescriptor(mars).getDescription(Locale.TAIWAN))
  }

  @Test
  fun placeOf() {
    assertEquals(2, Aversion.placeOf(0, 1))
    assertEquals(12, Aversion.placeOf(1, 0))
    assertEquals(8, Aversion.placeOf(7, 2))
    assertEquals(1, Aversion.placeOf(5, 5))
  }
}
