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
    pc.compare(Planet.SUN, Planet.MOON).let { assertTrue(it < 0) }
    pc.compare(Planet.MOON, Planet.JUPITER).let { assertTrue(it < 0) }
    pc.compare(Planet.JUPITER, Planet.SATURN).let { assertTrue(it < 0) }
    pc.compare(Planet.SATURN, Planet.PLUTO).let { assertTrue(it < 0) }

    pc.compare(LunarNode.SOUTH_MEAN, Asteroid.CERES).let { assertTrue(it < 0) }

    pc.compare(LunarNode.SOUTH_MEAN, Asteroid.PALLAS).let { assertTrue(it < 0) }

    pc.compare(Planet.SUN, LunarNode.NORTH_TRUE).let { assertTrue(it < 0) }

    pc.compare(Planet.MOON, LunarNode.SOUTH_MEAN).let { assertTrue(it < 0) }

    pc.compare(Asteroid.JUNO, FixedStar.ALGOL).let { assertTrue(it < 0) }

    pc.compare(Asteroid.VESTA, FixedStar.BETELGEUSE).let { assertTrue(it < 0) }

    pc.compare(FixedStar.ARCTURUS, Hamburger.APOLLON).let { assertTrue(it < 0) }

    pc.compare(FixedStar.FOMALHAUT, Hamburger.ADMETOS).let { assertTrue(it < 0) }
  }
}
