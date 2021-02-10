/**
 * Created by smallufo on 2019-11-22.
 */
package destiny.core.fengshui.sanyuan

import kotlin.test.Test
import kotlin.test.assertSame

class FlyingStarTest {

  @Test
  fun testGetValue_順推() {
    assertSame(5, FlyingStar.getValue(5, 0))
    assertSame(6, FlyingStar.getValue(5, 1))
    assertSame(9, FlyingStar.getValue(5, 4))
    assertSame(1, FlyingStar.getValue(5, 5))
    assertSame(9, FlyingStar.getValue(5, 13))
    assertSame(9, FlyingStar.getValue(5, 22))

    assertSame(1, FlyingStar.getValue(1, 0))
    assertSame(2, FlyingStar.getValue(1, 1))
    assertSame(9, FlyingStar.getValue(1, 8))
    assertSame(1, FlyingStar.getValue(1, 9))
    assertSame(9, FlyingStar.getValue(1, 17))
    assertSame(1, FlyingStar.getValue(1, 18))
    assertSame(1, FlyingStar.getValue(1, 27))
  }

  @Test
  fun testGetValue_逆推() {
    assertSame(5, FlyingStar.getValue(5, 0, true))
    assertSame(4, FlyingStar.getValue(5, 1, true))
    assertSame(1, FlyingStar.getValue(5, 4, true))
    assertSame(9, FlyingStar.getValue(5, 5, true))
    assertSame(5, FlyingStar.getValue(5, 9, true))
    assertSame(1, FlyingStar.getValue(5, 13, true))
    assertSame(9, FlyingStar.getValue(5, 14, true))
    assertSame(9, FlyingStar.getValue(5, 23, true))

    assertSame(1, FlyingStar.getValue(1, 0, true))
    assertSame(9, FlyingStar.getValue(1, 1, true))
    assertSame(2, FlyingStar.getValue(1, 8, true))
    assertSame(1, FlyingStar.getValue(1, 9, true))
    assertSame(9, FlyingStar.getValue(1, 10, true))
    assertSame(1, FlyingStar.getValue(1, 18, true))
    assertSame(1, FlyingStar.getValue(1, 27, true))
  }
}
