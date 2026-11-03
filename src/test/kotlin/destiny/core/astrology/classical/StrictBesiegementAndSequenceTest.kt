/**
 * Created by smallufo on 2026-11-04.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlin.test.*

/** 包夾的 [BesiegementOptions.STRICT]，以及受剋受扶的「離開一顆、迎上另一顆」（[Maltreatment.Condition.Sequence]） */
class StrictBesiegementAndSequenceTest {

  private fun model(lngs: Map<Planet, Double>, speeds: Map<Planet, Double> = emptyMap()): IHoroscopeModel {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = lngs.mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree(); every { lng } returns d } }
    return mockk {
      every { positionMap } returns posMap
      every { getPosition(any()) } answers { posMap[firstArg()] }
      Planet.classicalList.forEach { p ->
        every { getStarPosition(p) } returns speeds[p]?.let { v -> mockk<IStarPositionWithAzimuth> { every { speedLng } returns v } }
      }
    }
  }

  private val malefics = setOf(MARS, SATURN)

  /**
   * 金星巨蟹 10°（100°）；火星本身在 96°（前面 4°）；土星 223° 的三分光線落在 103°（後面 3°）。
   * 其餘的星（太陽 300、月亮 350、水星 310、木星 20）的星體與光線都不落在 96–103 之間
   */
  private val trineRay = mapOf(VENUS to 100.0, MARS to 96.0, SATURN to 223.0, SUN to 300.0, MOON to 350.0, MERCURY to 310.0, JUPITER to 20.0)

  @Test
  fun `STRICT 收凶星的三分光線 PORPHYRY 只收四分對沖`() {
    val b = BesiegementPorphyryImpl(BesiegementOptions.STRICT).getBesiegement(model(trineRay), VENUS, malefics)
    assertNotNull(b)
    assertEquals(MARS, b.before.by)
    assertNull(b.before.aspect, "火星以星體")
    assertEquals(SATURN, b.after.by)
    assertEquals(Aspect.TRINE, b.after.aspect)
    assertEquals(3.0, b.after.distance, 1e-9)
    assertNull(BesiegementPorphyryImpl(BesiegementOptions.PORPHYRY).getBesiegement(model(trineRay), VENUS, malefics), "舊判準不收凶星的三分")
  }

  /** 金星巨蟹 2°（92°）；火星在雙子 28°（88°，前面 4°）；土星 215° 的三分光線落在 95°（後面 3°） */
  @Test
  fun `STRICT 不允許換星座`() {
    val h = model(trineRay + (VENUS to 92.0) + (MARS to 88.0) + (SATURN to 215.0))
    assertNull(BesiegementPorphyryImpl(BesiegementOptions.STRICT).getBesiegement(h, VENUS, malefics), "火星那一側在雙子座")
    val crossing = BesiegementPorphyryImpl(BesiegementOptions.STRICT.copy(sameSign = false)).getBesiegement(h, VENUS, malefics)
    assertEquals(MARS, crossing?.before?.by, "允許換星座就成立")
  }

  @Test
  fun `預設值就是 STRICT`() {
    assertEquals(BesiegementOptions.STRICT, BesiegementOptions())
    assertTrue(BesiegementOptions.STRICT.sameSign && BesiegementOptions.STRICT.maleficRays == BesiegementOptions.PTOLEMAIC_RAYS)
    assertFalse(BesiegementOptions.PORPHYRY.sameSign)
  }

  // ======================== 離開一顆、迎上另一顆 ========================

  /**
   * 月亮巨蟹 10°（100°）、每天 13°：2° 前離開火星（8°）的四分，再 5° 迎上土星（225°）的三分；兩次準確時月亮都還在巨蟹座。
   * 其餘的星：太陽 300、水星 310 與月亮都不在容許度內；金星 50 與月亮差 50°，離六分 10°，超過兩星的星芒（12＋7）／2；木星 175 與四分、六分都差 15°
   */
  private val seqLngs = mapOf(MOON to 100.0, MARS to 8.0, SATURN to 225.0, SUN to 300.0, MERCURY to 310.0, VENUS to 50.0, JUPITER to 175.0)
  private val seqSpeeds = mapOf(MOON to 13.0, MARS to 0.5, SATURN to 0.1, SUN to 1.0, MERCURY to 1.2, VENUS to 1.2, JUPITER to 0.1)

  private fun sequenceOf(m: Maltreatment?) = m?.conditions?.filterIsInstance<Maltreatment.Condition.Sequence>()?.singleOrNull()

  private val impl = MaltreatmentAntiochusImpl(BesiegementPorphyryImpl(BesiegementOptions.STRICT))

  @Test
  fun `離開火星的四分 迎上土星的三分 是受剋`() {
    val s = sequenceOf(impl.getMaltreatment(model(seqLngs, seqSpeeds), MOON, Maltreatment.Polarity.MALTREATMENT))
    assertNotNull(s)
    assertEquals(listOf(MARS, SATURN), listOf(s.from, s.to))
    assertEquals(Aspect.SQUARE to Aspect.TRINE, s.fromAspect to s.toAspect)
    assertEquals(2.0, s.fromOrb, 1e-9)
    assertEquals(5.0, s.toOrb, 1e-9)
    assertNull(sequenceOf(impl.getMaltreatment(model(seqLngs, seqSpeeds), MOON, Maltreatment.Polarity.BONIFICATION)), "兩端都是凶星，不是受扶")
  }

  @Test
  fun `離開那一刻還在上一個星座就不算 除非允許換星座`() {
    // 月亮巨蟹 2°（92°）：4° 前（約 0.3 天前）離開火星（358°）的四分時，月亮還在雙子座
    val h = model(seqLngs + (MOON to 92.0) + (MARS to 358.0) + (SATURN to 217.0), seqSpeeds)
    assertNull(sequenceOf(impl.getMaltreatment(h, MOON, Maltreatment.Polarity.MALTREATMENT)))
    val loose = MaltreatmentAntiochusImpl(BesiegementPorphyryImpl(BesiegementOptions.STRICT), options = MaltreatmentOptions.BY_SIGN.copy(sequenceSameSign = false))
    assertEquals(MARS, sequenceOf(loose.getMaltreatment(h, MOON, Maltreatment.Polarity.MALTREATMENT))?.from)
  }

  @Test
  fun `中間先迎上別的星就不算`() {
    // 金星移到 43°：月亮與它差 57°，再 3° 就成六分（約 0.25 天後），比土星的三分（約 0.39 天後）更早 —— 下一個迎上的是金星，不是土星
    val h = model(seqLngs + (VENUS to 43.0), seqSpeeds)
    assertNull(sequenceOf(impl.getMaltreatment(h, MOON, Maltreatment.Polarity.MALTREATMENT)))
  }

  @Test
  fun `DTO 的條件 type 是 sequence`() {
    val c = Maltreatment.Condition.Sequence(MARS, Aspect.SQUARE, 2.0, SATURN, Aspect.TRINE, 5.0)
    val dto = Maltreatment(MOON, Maltreatment.Polarity.MALTREATMENT, listOf(c)).toChartDto() as HoroscopeMaltreatmentDto
    val json = Json.encodeToString(HoroscopeClassicalFactDto.serializer(), dto)
    assertTrue(""""type":"sequence"""" in json && """"fromAspect":"SQUARE"""" in json, json)
    assertEquals(listOf<AstroPoint>(MOON, MARS, SATURN), dto.members)
  }
}
