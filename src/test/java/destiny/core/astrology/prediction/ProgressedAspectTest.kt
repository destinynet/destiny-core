/**
 * Created by smallufo on 2022-08-03.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.IAspectData
import destiny.core.astrology.Planet
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class ProgressedAspectTest {

  @Test
  fun testInitFail() {
    assertFailsWith(IllegalArgumentException::class) {
      ProgressedAspect(Planet.MOON, Planet.SUN, 5.0, IAspectData.Type.APPLYING, null)
    }
  }

  @Test
  fun testInitOk() {
    ProgressedAspect(Planet.MOON, Planet.SUN, 0.0, IAspectData.Type.APPLYING, null)
  }
}
