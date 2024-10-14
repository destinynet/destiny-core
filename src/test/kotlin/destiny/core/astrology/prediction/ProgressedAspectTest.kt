/**
 * Created by smallufo on 2022-08-03.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.Planet
import kotlin.test.Test

internal class ProgressedAspectTest {

  @Test
  fun testInitOk() {
    SynastryAspect(Planet.MOON, Planet.SUN, 1, 1, Aspect.CONJUNCTION, 0.1, IPointAspectPattern.Type.APPLYING, null)
  }
}
