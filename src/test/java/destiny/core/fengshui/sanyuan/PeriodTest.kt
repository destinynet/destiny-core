/**
 * Created by smallufo on 2022-02-14.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PeriodTest {


  @Test
  fun testIntToPeriod() {
    assertEquals(1, 10.toPeriod().value)
    assertEquals(9, 18.toPeriod().value)
    assertEquals(1, 19.toPeriod().value)
    assertEquals(9, 0.toPeriod().value)
    assertEquals(8, (-1).toPeriod().value)
    assertEquals(1, (-8).toPeriod().value)
    assertEquals(9, (-9).toPeriod().value)
  }

  @Test
  fun testPlus() {
    assertEquals(Period.of(2), Period.of(1) + 1)
    assertEquals(Period.of(9), Period.of(1) + 8)
    assertEquals(Period.of(1), Period.of(1) + 9)
    assertEquals(Period.of(2), Period.of(1) + 10)
    assertEquals(Period.of(9), Period.of(1) + 17)
  }

  @Test
  fun testMinus() {
    assertEquals(Period.of(1) , Period.of(2) - 1)
    assertEquals(Period.of(9) , Period.of(2) - 2)
    assertEquals(Period.of(8) , Period.of(2) - 3)

    assertEquals(Period.of(1) , Period.of(9) - 8)
    assertEquals(Period.of(9) , Period.of(9) - 9)
    assertEquals(Period.of(9) , Period.of(9) - 18)
    assertEquals(Period.of(9) , Period.of(9) - 27)
  }
}

