/**
 * Created by smallufo on 2026-11-28.
 */
package destiny.core.fengshui

import destiny.core.iching.Symbol
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class Direction8Test {

  @Test
  fun `後天八卦的方位與羅盤一致`() {
    val compass = AcquiredSymbolCompass()
    Symbol.entries.forEach { s ->
      assertEquals(compass.getCenterDegree(s), s.direction8.degree, "$s")
      assertEquals(s.direction8, Direction8.of(compass.getCenterDegree(s)))
    }
  }

  @Test
  fun `方位角分到最近的方位`() {
    assertEquals(Direction8.N, Direction8.of(0.0))
    assertEquals(Direction8.N, Direction8.of(359.0))
    assertEquals(Direction8.N, Direction8.of(22.4))
    assertEquals(Direction8.NE, Direction8.of(22.5))
    assertEquals(Direction8.NW, Direction8.of(-30.0))
    assertEquals(Direction8.S, Direction8.of(540.0))
  }

  @Test
  fun `顯示名`() {
    assertEquals(listOf("北", "東北", "東", "東南", "南", "西南", "西", "西北"), Direction8.entries.map { it.getTitle(Locale.TAIWAN) })
    assertEquals("Northeast", Direction8.NE.getTitle(Locale.ENGLISH))
    assertEquals("东北", Direction8.NE.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("北東", Direction8.NE.getTitle(Locale.JAPANESE))
  }
}
