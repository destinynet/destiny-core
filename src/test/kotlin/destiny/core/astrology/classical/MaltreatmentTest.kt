/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPosWithAzimuth
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.Maltreatment.Condition
import destiny.core.astrology.classical.Maltreatment.Polarity
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.classical.rules.PatternTranslator
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.*

class MaltreatmentTest {

  /**
   * 太陽金牛 10°。土星水瓶 11°：四分居上太陽、以四分光線打中（差 1°，土星在太陽後方 89° 外的黃道前段 ⇒ sinister）；
   * 火星天蠍 11°：以星座對沖（差 1°）。木星金牛 12°：貼合 2°；金星巨蟹 10°：以六分光線打中（金星在太陽順黃道 60° 處 ⇒ dexter）
   */
  private val h: IHoroscopeModel = run {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(
      SUN to 40.0, SATURN to 311.0, MARS to 221.0, JUPITER to 42.0, VENUS to 100.0,
      MOON to 160.0, MERCURY to 75.0,
    ).mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    mockk { every { positionMap } returns posMap; every { getStarPosition(any()) } returns null }
  }

  private val noBesiegement = mockk<IBesiegement> { every { getBesiegement(any(), any(), any()) } returns null }

  @Test
  fun `受剋 被居上 光線 對沖 受扶 貼合 光線`() {
    val impl = MaltreatmentAntiochusImpl(noBesiegement)

    val mal = impl.getMaltreatment(h, SUN, Polarity.MALTREATMENT)!!
    assertEquals(setOf(SATURN, MARS), mal.sources)
    assertTrue(mal.conditions.any { it is Condition.Overcome && it.overcoming.superior == SATURN && it.overcoming.aspect == SQUARE })
    // 土星的四分光線是 sinister，預設只算 dexter ⇒ 不列；改成 EITHER 才列
    assertFalse(mal.conditions.any { it is Condition.RayHurled && it.source == SATURN })
    val either = MaltreatmentAntiochusImpl(noBesiegement, options = MaltreatmentOptions(rayDirection = RayDirection.EITHER))
    assertTrue(either.getMaltreatment(h, SUN, Polarity.MALTREATMENT)!!.conditions
      .any { it is Condition.RayHurled && it.source == SATURN && it.aspect == SQUARE && !it.dexter && abs1(it.orb) })
    assertTrue(mal.conditions.any { it is Condition.Opposed && it.source == MARS })
    // 對沖已列，不再以對沖光線重複
    assertFalse(mal.conditions.any { it is Condition.RayHurled && it.source == MARS })

    val ben = impl.getMaltreatment(h, SUN, Polarity.BONIFICATION)!!
    assertEquals(
      listOf(Condition.RayHurled(VENUS, SEXTILE, 0.0, dexter = true), Condition.Adherence(JUPITER, 2.0)).map { it::class }.toSet(),
      ben.conditions.map { it::class }.toSet()
    )
    assertTrue(ben.conditions.any { it is Condition.RayHurled && it.source == VENUS && it.dexter })

    val zh = PatternTranslator.getDescriptor(Misc.Maltreated(SUN, mal)).getDescription(Locale.TAIWAN)
    assertTrue("太陽 受剋" in zh && "被 土星 四分居上" in zh && "與 火星 對沖" in zh, zh)
    val en = PatternTranslator.getDescriptor(Misc.Maltreated(SUN, ben)).getDescription(Locale.ENGLISH)
    assertTrue("Sun is bonified" in en && "in adherence with Jupiter" in en, en)
  }

  @Test
  fun `選項 光線只算 dexter 對沖改看度數`() {
    val impl = MaltreatmentAntiochusImpl(noBesiegement, options = MaltreatmentOptions(oppositionOrb = 0.5))
    val mal = impl.getMaltreatment(h, SUN, Polarity.MALTREATMENT)!!
    assertFalse(mal.conditions.any { it is Condition.RayHurled && it.source == SATURN }, "土星的光線是 sinister")
    assertFalse(mal.conditions.any { it is Condition.Opposed }, "火星對沖差 1°，超過 0.5°")
    // 不列為對沖之後，火星改以對沖的光線出現（火星在太陽順黃道 181° 處 ⇒ dexter）
    assertTrue(mal.conditions.any { it is Condition.RayHurled && it.source == MARS && it.aspect == OPPOSITION && it.dexter })
  }

  @Test
  fun `施予者不會處置自己 關掉全部條件就沒有結果`() {
    val impl = MaltreatmentAntiochusImpl(noBesiegement, options = MaltreatmentOptions(maleficConditions = emptySet()))
    assertNull(impl.getMaltreatment(h, SUN, Polarity.MALTREATMENT))
    val saturn = MaltreatmentAntiochusImpl(noBesiegement).getMaltreatment(h, SATURN, Polarity.MALTREATMENT)
    assertTrue(saturn == null || SATURN !in saturn.sources)
  }

  /** 與吉星對沖不算受扶（預設）；把對沖加回受扶的條件就算 */
  @Test
  fun `與吉星對沖預設不算受扶`() {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(MOON to 132.0, JUPITER to 322.0)
      .mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    val chart = mockk<IHoroscopeModel> { every { positionMap } returns posMap; every { getStarPosition(any()) } returns null }
    assertNull(MaltreatmentAntiochusImpl(noBesiegement).getMaltreatment(chart, MOON, Polarity.BONIFICATION))
    val withOpposition = MaltreatmentOptions(beneficConditions = MaltreatmentOptions.ConditionType.entries.toSet())
    assertNotNull(MaltreatmentAntiochusImpl(noBesiegement, options = withOpposition).getMaltreatment(chart, MOON, Polarity.BONIFICATION))
  }

  /** 選項的居上基準與注入的 [IOvercoming] 不一致時，建構就失敗（BY_DEGREE 配星座版居上，會默默以星座判） */
  @Test
  fun `居上基準與注入的 IOvercoming 不一致時建構失敗`() {
    assertFailsWith<IllegalArgumentException> {
      MaltreatmentAntiochusImpl(noBesiegement, OvercomingSignImpl(), MaltreatmentOptions.BY_DEGREE)
    }
    MaltreatmentAntiochusImpl(noBesiegement, OvercomingDegreeImpl(), MaltreatmentOptions.BY_DEGREE)
    MaltreatmentAntiochusImpl(noBesiegement, OvercomingSignImpl(), MaltreatmentOptions.BY_SIGN)
  }

  private fun abs1(orb: Double) = orb in 0.99..1.01
}
