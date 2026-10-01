/**
 * Created by smallufo on 2026-10-01.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect.*
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPosWithAzimuth
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.Misc
import io.mockk.every
import io.mockk.mockk
import kotlin.test.*

class OvercomingTest {

  private val signImpl = OvercomingSignImpl()
  private val degreeImpl = OvercomingDegreeImpl()

  /** 星座 index (0 = 牡羊) + 度數 */
  private fun deg(signIndex: Int, d: Double) = (signIndex * 30 + d).toZodiacDegree()

  @Test
  fun `火星摩羯 四分居上 金星白羊 , 星座與度數皆成立`() {
    val mars = deg(9, 10.0)
    val venus = deg(0, 12.0)

    listOf(signImpl, degreeImpl).forEach { impl ->
      // 參數順序不影響結果
      listOf(
        impl.getOvercoming(MARS, mars, VENUS, venus),
        impl.getOvercoming(VENUS, venus, MARS, mars)
      ).forEach { o ->
        assertNotNull(o)
        assertSame(MARS, o.superior)
        assertSame(VENUS, o.inferior)
        assertSame(SQUARE, o.aspect)
        assertTrue(o.decimation)
        assertEquals(2.0, o.orb, 0.001)
        assertTrue(o.bySign)
        assertTrue(o.byDegree)
        assertNotNull(o.score)
        assertSame(impl.basis, o.basis)
      }
    }
  }

  @Test
  fun `摩羯0度 對 白羊29度 , 星座四分居上 , 度數上是三分`() {
    val mars = deg(9, 0.0)
    val venus = deg(0, 29.0)

    signImpl.getOvercoming(MARS, mars, VENUS, venus).also { o ->
      assertNotNull(o)
      assertSame(MARS, o.superior)
      assertSame(SQUARE, o.aspect)
      assertEquals(29.0, o.orb, 0.001)
      assertTrue(o.bySign)
      assertFalse(o.byDegree, "名義上居上 , 度數上未成四分")
      assertNull(o.score)
    }

    degreeImpl.getOvercoming(MARS, mars, VENUS, venus).also { o ->
      assertNotNull(o)
      assertSame(MARS, o.superior)
      assertSame(TRINE, o.aspect)
      assertEquals(1.0, o.orb, 0.001)
      assertFalse(o.bySign, "dissociate")
      assertTrue(o.byDegree)
    }
  }

  @Test
  fun `摩羯29度 對 金牛1度 , 星座三分 , 度數為跨星座的四分`() {
    val mars = deg(9, 29.0)
    val venus = deg(1, 1.0)

    signImpl.getOvercoming(MARS, mars, VENUS, venus).also { o ->
      assertNotNull(o)
      assertSame(MARS, o.superior)
      assertSame(TRINE, o.aspect)
      assertFalse(o.byDegree)
    }

    degreeImpl.getOvercoming(MARS, mars, VENUS, venus).also { o ->
      assertNotNull(o)
      assertSame(MARS, o.superior)
      assertSame(SQUARE, o.aspect)
      assertEquals(2.0, o.orb, 0.001)
      assertFalse(o.bySign)
    }
  }

  @Test
  fun `跨越 雙魚到牡羊 的換圈`() {
    // 雙魚 10 度 , 雙子 10 度 : 雙魚 居上 (四分)
    val saturn = deg(11, 10.0)
    val moon = deg(2, 10.0)
    listOf(signImpl, degreeImpl).forEach { impl ->
      val o = impl.getOvercoming(MOON, moon, SATURN, saturn)
      assertNotNull(o)
      assertSame(SATURN, o.superior)
      assertSame(MOON, o.inferior)
      assertSame(SQUARE, o.aspect)
    }
  }

  @Test
  fun `六分 與 三分 居上`() {
    // 金星 水瓶 5 度 , 火星 牡羊 5 度 : 金星 六分居上
    listOf(signImpl, degreeImpl).forEach { impl ->
      val o = impl.getOvercoming(MARS, deg(0, 5.0), VENUS, deg(10, 5.0))
      assertNotNull(o)
      assertSame(VENUS, o.superior)
      assertSame(SEXTILE, o.aspect)
      assertFalse(o.decimation)
    }

    // 木星 射手 10 度 , 土星 牡羊 10 度 : 木星 三分居上
    listOf(signImpl, degreeImpl).forEach { impl ->
      val o = impl.getOvercoming(SATURN, deg(0, 10.0), JUPITER, deg(8, 10.0))
      assertNotNull(o)
      assertSame(JUPITER, o.superior)
      assertSame(TRINE, o.aspect)
    }
  }

  @Test
  fun `合相 對沖 十二分 無居上`() {
    listOf(signImpl, degreeImpl).forEach { impl ->
      assertNull(impl.getOvercoming(SUN, deg(0, 10.0), MOON, deg(0, 12.0)), "合相")
      assertNull(impl.getOvercoming(SUN, deg(0, 10.0), MOON, deg(6, 10.0)), "對沖")
      assertNull(impl.getOvercoming(SUN, deg(0, 10.0), MOON, deg(1, 10.0)), "十二分")
      assertNull(impl.getOvercoming(SUN, deg(0, 10.0), MOON, deg(5, 10.0)), "補十二分")
    }
  }

  @Test
  fun `度數版 出了容許度 無居上`() {
    // 火星 + 金星 容許度 (8+7)/2 = 7.5 , 角距 100 度
    assertNull(degreeImpl.getOvercoming(MARS, deg(9, 0.0), VENUS, deg(0, 10.0)))
    // 星座版仍成立
    assertNotNull(signImpl.getOvercoming(MARS, deg(9, 0.0), VENUS, deg(0, 10.0)))
  }

  @Test
  fun `一張盤兩兩計算`() {
    val posMap = mapOf<destiny.core.astrology.AstroPoint, destiny.core.astrology.IZodiacDegree>(
      MARS to deg(9, 10.0),
      VENUS to deg(0, 12.0),
      JUPITER to deg(3, 15.0),  // 巨蟹 : 被 金星(牡羊) 四分居上 , 與 火星 對沖
    )
    val result = signImpl.getOvercomings(posMap.keys, posMap)
    assertEquals(2, result.size)
    assertTrue(result.any { it.superior == MARS && it.inferior == VENUS && it.aspect == SQUARE })
    assertTrue(result.any { it.superior == VENUS && it.inferior == JUPITER && it.aspect == SQUARE })
  }

  @Test
  fun `本命 pattern factory 兩方都會列出 , 並標示是否居上`() {
    val posMap: Map<destiny.core.astrology.AstroPoint, IPosWithAzimuth> = mapOf(
      MARS to deg(9, 10.0),
      VENUS to deg(0, 12.0),
    ).mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d } }

    val h = mockk<IHoroscopeModel> { every { positionMap } returns posMap }
    val ctx = ClassicalPatternContext(
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true)
    )
    val factory = ctx.overcomingBySign

    // 不在任何計分清單中
    assertFalse(ctx.essentialDignities.contains(factory))
    assertFalse(ctx.accidentalDignities.contains(factory))
    assertFalse(ctx.debilities.contains(factory))

    val marsPatterns = factory.getPatterns(MARS, h).filterIsInstance<Misc.Overcoming>()
    assertEquals(1, marsPatterns.size)
    assertTrue(marsPatterns.single().superior)
    assertSame(destiny.core.astrology.classical.rules.RuleType.MISC, marsPatterns.single().ruleType)

    val venusPatterns = factory.getPatterns(VENUS, h).filterIsInstance<Misc.Overcoming>()
    assertEquals(1, venusPatterns.size)
    assertFalse(venusPatterns.single().superior)
  }

  @Test
  fun `SynastryOvercoming 可序列化 , 並由 side 還原 outer inner`() {
    val o = signImpl.getOvercoming(MARS, deg(9, 10.0), VENUS, deg(0, 12.0))!!
    val so = destiny.core.astrology.SynastryOvercoming(destiny.core.astrology.SynastryOvercoming.Side.OUTER, o)
    assertSame(MARS, so.outerPoint)
    assertSame(VENUS, so.innerPoint)

    val json = kotlinx.serialization.json.Json.encodeToString(destiny.core.astrology.SynastryOvercoming.serializer(), so)
    val back = kotlinx.serialization.json.Json.decodeFromString(destiny.core.astrology.SynastryOvercoming.serializer(), json)
    assertEquals(so.superiorSide, back.superiorSide)
    assertEquals(so.overcoming.superior, back.overcoming.superior)
    assertEquals(so.overcoming.aspect, back.overcoming.aspect)
    assertEquals(so.overcoming.bySign, back.overcoming.bySign)
    assertEquals(so.overcoming.byDegree, back.overcoming.byDegree)
  }
}
