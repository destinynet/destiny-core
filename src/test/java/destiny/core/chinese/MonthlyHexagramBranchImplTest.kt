/**
 * Created by smallufo on 2019-04-03.
 */
package destiny.core.chinese

import destiny.core.calendar.SolarTerms.*
import destiny.iching.Hexagram
import kotlin.test.Test
import kotlin.test.assertEquals


class MonthlyHexagramBranchImplTest {

  val impl = MonthlyHexagramBranchImpl()

  @Test
  fun getHexagram() {
    assertEquals(Hexagram.坤 to (立冬 to 大雪) , impl.getHexagram(小雪))

    assertEquals(Hexagram.復 to (大雪 to 小寒) , impl.getHexagram(大雪))
    assertEquals(Hexagram.復 to (大雪 to 小寒) , impl.getHexagram(冬至))

    assertEquals(Hexagram.臨 to (小寒 to 立春) , impl.getHexagram(小寒))
    assertEquals(Hexagram.臨 to (小寒 to 立春) , impl.getHexagram(大寒))

    assertEquals(Hexagram.乾 to (立夏 to 芒種) , impl.getHexagram(立夏))
    assertEquals(Hexagram.乾 to (立夏 to 芒種) , impl.getHexagram(小滿))

    assertEquals(Hexagram.姤 to (芒種 to 小暑) , impl.getHexagram(芒種))
    assertEquals(Hexagram.姤 to (芒種 to 小暑) , impl.getHexagram(夏至))

    assertEquals(Hexagram.觀 to (白露 to 寒露) , impl.getHexagram(白露))


  }
}