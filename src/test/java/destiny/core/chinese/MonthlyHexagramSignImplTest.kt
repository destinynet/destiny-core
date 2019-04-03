/**
 * Created by smallufo on 2019-04-03.
 */
package destiny.core.chinese

import destiny.core.calendar.SolarTerms.*
import destiny.iching.Hexagram
import kotlin.test.Test
import kotlin.test.assertEquals


class MonthlyHexagramSignImplTest {

  val impl = MonthlyHexagramSignImpl()

  @Test
  fun getHexagram() {
    assertEquals(Hexagram.坤 to (小雪 to 冬至) , impl.getHexagram(小雪))
    assertEquals(Hexagram.坤 to (小雪 to 冬至) , impl.getHexagram(大雪))

    assertEquals(Hexagram.復 to (冬至 to 大寒) , impl.getHexagram(冬至))
    assertEquals(Hexagram.復 to (冬至 to 大寒) , impl.getHexagram(小寒))

    assertEquals(Hexagram.臨 to (大寒 to 雨水) , impl.getHexagram(大寒))
    assertEquals(Hexagram.臨 to (大寒 to 雨水) , impl.getHexagram(立春))


    assertEquals(Hexagram.乾 to (小滿 to 夏至) , impl.getHexagram(小滿))
    assertEquals(Hexagram.乾 to (小滿 to 夏至) , impl.getHexagram(芒種))

    assertEquals(Hexagram.姤 to (夏至 to 大暑) , impl.getHexagram(夏至))
    assertEquals(Hexagram.姤 to (夏至 to 大暑) , impl.getHexagram(小暑))

    assertEquals(Hexagram.遯 to (大暑 to 處暑) , impl.getHexagram(大暑))
    assertEquals(Hexagram.遯 to (大暑 to 處暑) , impl.getHexagram(立秋))
  }
}