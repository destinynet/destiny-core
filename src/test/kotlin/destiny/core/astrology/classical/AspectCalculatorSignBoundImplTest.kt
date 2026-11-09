/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.AspectCalculatorImpl
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IPos
import destiny.core.astrology.Planet.JUPITER
import destiny.core.astrology.Planet.MARS
import destiny.core.astrology.Planet.SATURN
import destiny.core.astrology.Pos
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * 火星摩羯 26°、土星牡羊 4°：相距 68°，依容許度是六合，依星座是四分 —— 以星座計的 calculator 不留。
 * 木星雙子 2°：與土星相距 58°，依容許度、依星座都是六合 —— 照留
 */
class AspectCalculatorSignBoundImplTest {

  private val plain = AspectCalculatorImpl(AspectEffectiveClassical(), emptyMap())
  private val bound = AspectCalculatorSignBoundImpl(plain)

  private val positions: Map<AstroPoint, IPos> = mapOf(MARS to Pos(296.0, 0.0), SATURN to Pos(4.0, 0.0), JUPITER to Pos(62.0, 0.0))
  private val aspects = Aspect.getAspects(Aspect.Importance.HIGH)

  @Test
  fun crossSignAspectDropped() {
    assertEquals(Aspect.SEXTILE, plain.getAspectPattern(MARS, SATURN, positions, positions, null, null, aspects)?.aspect)
    assertNull(bound.getAspectPattern(MARS, SATURN, positions, positions, null, null, aspects))
  }

  @Test
  fun sameAspectBySignKept() {
    val expected = assertNotNull(plain.getAspectPattern(JUPITER, SATURN, positions, positions, null, null, aspects))
    assertEquals(Aspect.SEXTILE, expected.aspect)
    assertEquals(expected, bound.getAspectPattern(JUPITER, SATURN, positions, positions, null, null, aspects))
  }

  @Test
  fun pointAspects() {
    assertEquals(mapOf<AstroPoint, Aspect>(MARS to Aspect.SEXTILE, JUPITER to Aspect.SEXTILE), plain.getPointAspect(SATURN, positions, positions.keys, aspects))
    assertEquals(mapOf<AstroPoint, Aspect>(JUPITER to Aspect.SEXTILE), bound.getPointAspect(SATURN, positions, positions.keys, aspects))
    // 預設實作（以 positionMap 算全部交角）走的也是篩過的那一份
    assertEquals(
      setOf(setOf(JUPITER, SATURN)),
      bound.getAspectPatterns(positions, positions.keys, aspects).map { it.points.toSet() }.toSet()
    )
  }
}
