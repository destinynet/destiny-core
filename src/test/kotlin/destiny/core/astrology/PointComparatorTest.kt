/**
 * @author smallufo
 * Created on 2010/7/4 at 下午6:45:33
 */
package destiny.core.astrology

import kotlin.test.Test
import kotlin.test.assertTrue

class PointComparatorTest {
  private val pc = AstroPointComparator

  @Test
  fun testCompare() {
    pc.compare(Planet.SUN, Planet.MOON).also { assertTrue(it < 0) }
    pc.compare(Planet.MOON, Planet.JUPITER).also { assertTrue(it < 0) }
    pc.compare(Planet.JUPITER, Planet.SATURN).also { assertTrue(it < 0) }
    pc.compare(Planet.SATURN, Planet.PLUTO).also { assertTrue(it < 0) }


    pc.compare(LunarNode.NORTH_TRUE, LunarNode.SOUTH_TRUE).also { assertTrue(it < 0) }
    pc.compare(LunarNode.NORTH_MEAN, LunarNode.SOUTH_MEAN).also { assertTrue(it < 0) }

    pc.compare(LunarNode.SOUTH_TRUE, Asteroid.CERES).also { assertTrue(it < 0) }
    pc.compare(LunarNode.SOUTH_MEAN, Asteroid.CERES).also { assertTrue(it < 0) }

    pc.compare(LunarNode.SOUTH_TRUE, Asteroid.PALLAS).also { assertTrue(it < 0) }
    pc.compare(LunarNode.SOUTH_MEAN, Asteroid.PALLAS).also { assertTrue(it < 0) }

    pc.compare(Planet.SUN, LunarNode.NORTH_TRUE).also { assertTrue(it < 0) }

    pc.compare(Planet.MOON, LunarNode.SOUTH_TRUE).also { assertTrue(it < 0) }
    pc.compare(Planet.MOON, LunarNode.SOUTH_MEAN).also { assertTrue(it < 0) }

    pc.compare(Asteroid.JUNO, FixedStar.ALGOL).also { assertTrue(it < 0) }

    pc.compare(Asteroid.VESTA, FixedStar.BETELGEUSE).also { assertTrue(it < 0) }

    pc.compare(FixedStar.ARCTURUS, Hamburger.APOLLON).also { assertTrue(it < 0) }

    pc.compare(FixedStar.FOMALHAUT, Hamburger.ADMETOS).also { assertTrue(it < 0) }
  }
}
