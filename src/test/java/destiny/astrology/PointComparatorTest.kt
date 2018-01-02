/**
 * @author smallufo
 * Created on 2010/7/4 at 下午6:45:33
 */
package destiny.astrology

import kotlin.test.Test
import kotlin.test.assertTrue

class PointComparatorTest {
  private val pc = PointComparator()

  @Test
  fun testCompare() {
    assertTrue(pc.compare(LunarNode.SOUTH_MEAN, Asteroid.CERES) < 0)
    assertTrue(pc.compare(LunarNode.SOUTH_MEAN, Asteroid.PALLAS) < 0)

    assertTrue(pc.compare(Planet.SUN, LunarNode.NORTH_TRUE) < 0)
    assertTrue(pc.compare(Planet.MOON, LunarNode.SOUTH_MEAN) < 0)

    assertTrue(pc.compare(Asteroid.JUNO, FixedStar.ALGOL) < 0)
    assertTrue(pc.compare(Asteroid.VESTA, FixedStar.BETELGEUSE) < 0)

    assertTrue(pc.compare(FixedStar.ARCTURUS, Hamburger.APOLLON) < 0)
    assertTrue(pc.compare(FixedStar.FOMALHAUT, Hamburger.ADMETOS) < 0)
  }
}
