/**
 * Created by smallufo on 2022-02-14.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.sanyuan.Period.*
import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class PeriodTest {

  @Test
  fun testCompare() {
    val shuffled = Period.values().toList().shuffled()
    assertEquals(Period.values().toList() , shuffled.sorted())
  }

  @Test
  fun testIntToPeriod() {
    assertSame(1, 10.toPeriod().value)
    assertSame(9, 18.toPeriod().value)
    assertSame(1, 19.toPeriod().value)
    assertSame(9, 0.toPeriod().value)
    assertSame(8, (-1).toPeriod().value)
    assertSame(1, (-8).toPeriod().value)
    assertSame(9, (-9).toPeriod().value)
  }

  @Test
  fun testPlus() {
    assertSame(P2, P1 + 1)
    assertSame(P9, P1 + 8)
    assertSame(P1, P1 + 9)
    assertSame(P2, P1 + 10)
    assertSame(P9, P1 + 17)
  }

  @Test
  fun testMinus() {
    assertSame(P1 , P2 - 1)
    assertSame(P9 , P2 - 2)
    assertSame(P8 , P2 - 3)

    assertSame(P1 , P9 - 8)
    assertSame(P9 , P9 - 9)
    assertSame(P9 , P9 - 18)
    assertSame(P9 , P9 - 27)
  }
}

