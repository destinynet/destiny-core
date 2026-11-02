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
import destiny.core.astrology.ZodiacSign
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

  /**
   * 選項：映點星座的例外、入旺主。金星在雙魚、雙魚之主木星在天秤：雙魚在天秤起算的第 6 個 ⇒ 不相見；但雙魚的映點星座正是天秤（彼此看得見），收 BEHOLDING 就不算。
   * 太陽金牛、入旺主月亮在射手：金牛在射手起算的第 6 個 ⇒ 與入旺主不相見
   */
  @Test
  fun `選項 映點星座例外 入旺主`() {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(VENUS to deg(11, 10.0), JUPITER to deg(6, 10.0), SUN to deg(1, 10.0), MOON to deg(8, 10.0))
      .mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d } }
    val h = mockk<IHoroscopeModel> {
      every { positionMap } returns posMap
      every { cuspDegreeMap } returns emptyMap()
    }
    val plain = AversionSignImpl(RulerPtolemyImpl, ExaltationPtolemyImpl).getResults(h)
    assertTrue(plain.any { it.planet == VENUS && it.from == JUPITER && it.place == 6 })

    val beholding = AversionSignImpl(RulerPtolemyImpl, ExaltationPtolemyImpl, AversionOptions(exceptions = setOf(SignLink.BEHOLDING))).getResults(h)
    assertFalse(beholding.any { it.planet == VENUS && it.from == JUPITER }, "雙魚與天秤彼此看得見")

    val exalt = AversionSignImpl(RulerPtolemyImpl, ExaltationPtolemyImpl, AversionOptions(bases = setOf(Aversion.Basis.EXALTATION))).getResults(h)
    val sun = exalt.single { it.planet == SUN }
    assertEquals(MOON to Aversion.Basis.EXALTATION, sun.from to sun.basis)
    assertTrue("入旺主" in PatternTranslator.getDescriptor(Misc.Aversion(SUN, sun)).getDescription(Locale.TAIWAN))
  }

  /** Ptolemy 的經典例：金星在牡羊、牡羊之主火星在處女 —— 第 8 個星座（不相見），但牡羊↔處女是映點星座；希臘化預設照算不相見 */
  @Test
  fun `流派 希臘化與 Ptolemy`() {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(VENUS to deg(0, 10.0), MARS to deg(5, 10.0))
      .mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d } }
    val h = mockk<IHoroscopeModel> {
      every { positionMap } returns posMap
      every { cuspDegreeMap } returns emptyMap()
    }
    val venus = { o: AversionOptions -> AversionSignImpl(RulerPtolemyImpl, ExaltationPtolemyImpl, o).getResults(h).filter { it.planet == VENUS } }
    assertEquals(listOf(8), venus(AversionOptions.HELLENISTIC).map { it.place })
    assertTrue(venus(AversionOptions.PTOLEMY).isEmpty())
    assertSame(AversionOptions.HELLENISTIC, AversionOptions.DEFAULT)
  }

  @Test
  fun `映點星座`() {
    assertEquals(ZodiacSign.CANCER, ZodiacSign.GEMINI.antiscionSign)
    assertEquals(ZodiacSign.LIBRA, ZodiacSign.PISCES.antiscionSign)
    assertEquals(ZodiacSign.PISCES, ZodiacSign.ARIES.contraAntiscionSign)
    assertEquals(ZodiacSign.AQUARIUS, ZodiacSign.TAURUS.contraAntiscionSign)
  }

  /** 不相見的位置數法與 [destiny.core.astrology.ZodiacSign.placeFrom] 一致（起算的星座算第 1 個） */
  @Test
  fun placeFrom() {
    assertEquals(2, ZodiacSign.TAURUS.placeFrom(ZodiacSign.ARIES))
    assertEquals(12, ZodiacSign.ARIES.placeFrom(ZodiacSign.TAURUS))
    assertEquals(8, ZodiacSign.GEMINI.placeFrom(ZodiacSign.SCORPIO))
    assertEquals(1, ZodiacSign.VIRGO.placeFrom(ZodiacSign.VIRGO))
  }
}
