/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.astrology

import kotlin.test.Test
import kotlin.test.assertEquals

class AnglesFromMidheavenTest {

  @Test
  fun `赤道、黃赤交角 0：上升恰在天頂前 90 度`() {
    listOf(0.0, 45.0, 170.0, 300.0).forEach { mc ->
      assertEquals((mc + 90).mod(360.0), ascendantFromMidheaven(mc, 0.0, 0.0), 1e-9)
    }
  }

  @Test
  fun `赤道上：天頂在二分點時上升在二至點`() {
    assertEquals(90.0, ascendantFromMidheaven(0.0, 23.44, 0.0), 1e-9)
    assertEquals(270.0, ascendantFromMidheaven(180.0, 23.44, 0.0), 1e-9)
  }

  @Test
  fun `北緯 40 度：天頂牡羊 0 度時上升約巨蟹 18 度（宮位表的值）`() {
    assertEquals(108.46, ascendantFromMidheaven(0.0, 23.44, 40.0), 0.05)
  }
}
