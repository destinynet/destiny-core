/**
 * Created by smallufo on 2026-11-17.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.LunarNode
import destiny.core.astrology.Planet
import kotlin.test.Test
import kotlin.test.assertEquals

class FirdariaSequenceTest {

  @Test
  fun `日生盤兩派相同`() {
    assertEquals(FirdariaAbuMashar.majorRulers(true), FirdariaBonatti.majorRulers(true))
    assertEquals(listOf(Planet.SUN, Planet.VENUS, Planet.MERCURY, Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, LunarNode.NORTH, LunarNode.SOUTH),
                 FirdariaAbuMashar.majorRulers(true))
  }

  @Test
  fun `Abu Ma'shar 夜生盤交點排最後`() {
    assertEquals(listOf(Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, Planet.SUN, Planet.VENUS, Planet.MERCURY, LunarNode.NORTH, LunarNode.SOUTH),
                 FirdariaAbuMashar.majorRulers(false))
  }

  @Test
  fun `Bonatti 夜生盤交點接在火星之後`() {
    assertEquals(listOf(Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, LunarNode.NORTH, LunarNode.SOUTH, Planet.SUN, Planet.VENUS, Planet.MERCURY),
                 FirdariaBonatti.majorRulers(false))
  }

  @Test
  fun `兩派的七星順序相同、一輪都是 75 年`() {
    listOf(true, false).forEach { diurnal ->
      assertEquals(FirdariaAbuMashar.majorRulers(diurnal).filterIsInstance<Planet>(), FirdariaBonatti.majorRulers(diurnal).filterIsInstance<Planet>())
      listOf(FirdariaAbuMashar, FirdariaBonatti).forEach { seq ->
        assertEquals(75, seq.majorRulers(diurnal).sumOf { majorRulerYearsMap.getValue(it) })
      }
    }
  }

  @Test
  fun `variant 與實作互相對應，預設 Abu Ma'shar`() {
    FirdariaVariant.entries.forEach { assertEquals(it, it.sequence.variant) }
    assertEquals(FirdariaAbuMashar, FirdariaVariant.DEFAULT.sequence)
  }
}
