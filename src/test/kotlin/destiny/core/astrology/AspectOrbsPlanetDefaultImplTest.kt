/**
 * @author smallufo
 */
package destiny.core.astrology

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AspectOrbsPlanetDefaultImplTest  {

  private val impl: IAspectOrbsPlanet = AspectOrbsPlanetDefaultImpl()

  @Test
  fun testGetPlanetOrb() {
    assertEquals(12.0, impl.getPlanetAspectOrb(Planet.SUN, Planet.MOON, Aspect.CONJUNCTION))
    assertEquals(12.0, impl.getPlanetAspectOrb(Planet.MOON, Planet.SUN, Aspect.CONJUNCTION))
    assertNull(impl.getPlanetAspectOrb(Planet.SUN, Planet.VENUS, Aspect.CONJUNCTION))

    assertEquals(12.0, impl.getPlanetAspectOrb(Planet.SUN, Planet.MOON, Aspect.OPPOSITION))
    assertEquals(12.0, impl.getPlanetAspectOrb(Planet.MOON, Planet.SUN, Aspect.OPPOSITION))
  }

}
