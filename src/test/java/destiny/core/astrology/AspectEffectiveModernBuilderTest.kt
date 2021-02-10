/**
 * Created by smallufo on 2020-10-19.
 */
package destiny.core.astrology

import destiny.core.astrology.AspectEffectiveModernBuilder.Companion.aspectEffectiveModern
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AspectEffectiveModernBuilderTest {

  @Test
  fun equals_with_default() {
    val impl1 = aspectEffectiveModern {}
    val impl2 = aspectEffectiveModern {
      aspectOrbsImpl = AspectOrbsDefaultImpl(0.9)
      aspectOrbsPlanetImpl = AspectOrbsPlanetDefaultImpl()
    }
    assertEquals(impl1, impl2)
  }

  @Test
  fun notEquals_by_defaultThreshold() {
    val impl1 = aspectEffectiveModern {}
    val impl2 = aspectEffectiveModern {
      aspectOrbsImpl = AspectOrbsDefaultImpl(0.8)
    }
    assertNotEquals(impl1, impl2)
  }

}
