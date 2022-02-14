/**
 * Created by smallufo on 2019-11-22.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import kotlin.test.Test
import kotlin.test.assertEquals

class FlyingStarTest {

  @Test
  fun testGetValue_順推() {
    assertEquals(5.toPeriod(), FlyingStar.getValue(5.toPeriod(), 0))
    assertEquals(6.toPeriod(), FlyingStar.getValue(5.toPeriod(), 1))
    assertEquals(9.toPeriod(), FlyingStar.getValue(5.toPeriod(), 4))
    assertEquals(1.toPeriod(), FlyingStar.getValue(5.toPeriod(), 5))
    assertEquals(9.toPeriod(), FlyingStar.getValue(5.toPeriod(), 13))
    assertEquals(9.toPeriod(), FlyingStar.getValue(5.toPeriod(), 22))

    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 0))
    assertEquals(2.toPeriod(), FlyingStar.getValue(1.toPeriod(), 1))
    assertEquals(9.toPeriod(), FlyingStar.getValue(1.toPeriod(), 8))
    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 9))
    assertEquals(9.toPeriod(), FlyingStar.getValue(1.toPeriod(), 17))
    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 18))
    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 27))
  }

  @Test
  fun testGetValue_逆推() {
    assertEquals(5.toPeriod(), FlyingStar.getValue(5.toPeriod(), 0, true))
    assertEquals(4.toPeriod(), FlyingStar.getValue(5.toPeriod(), 1, true))
    assertEquals(1.toPeriod(), FlyingStar.getValue(5.toPeriod(), 4, true))
    assertEquals(9.toPeriod(), FlyingStar.getValue(5.toPeriod(), 5, true))
    assertEquals(5.toPeriod(), FlyingStar.getValue(5.toPeriod(), 9, true))
    assertEquals(1.toPeriod(), FlyingStar.getValue(5.toPeriod(), 13, true))
    assertEquals(9.toPeriod(), FlyingStar.getValue(5.toPeriod(), 14, true))
    assertEquals(9.toPeriod(), FlyingStar.getValue(5.toPeriod(), 23, true))

    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 0, true))
    assertEquals(9.toPeriod(), FlyingStar.getValue(1.toPeriod(), 1, true))
    assertEquals(2.toPeriod(), FlyingStar.getValue(1.toPeriod(), 8, true))
    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 9, true))
    assertEquals(9.toPeriod(), FlyingStar.getValue(1.toPeriod(), 10, true))
    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 18, true))
    assertEquals(1.toPeriod(), FlyingStar.getValue(1.toPeriod(), 27, true))
  }
}
