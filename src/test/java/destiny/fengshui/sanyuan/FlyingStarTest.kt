/**
 * Created by smallufo on 2019-11-22.
 */
package destiny.fengshui.sanyuan

import kotlin.test.Test
import kotlin.test.assertSame

class FlyingStarTest {

  @Test
  fun testGetValue() {
    assertSame(5, FlyingStar.getValue(5, 0, false))
    assertSame(6, FlyingStar.getValue(5, 1, false))
    assertSame(9, FlyingStar.getValue(5, 4, false))
    assertSame(1, FlyingStar.getValue(5, 5, false))
  }
}
