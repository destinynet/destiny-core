/**
 * Created by kevin.huang on 2019-09-27.
 */
package destiny.astrology.classical

import destiny.astrology.Planet
import kotlin.test.Test
import kotlin.test.assertEquals

class MutualDataTest {

  @Test
  fun testEqual() {
    val m1 = MutualData(Planet.SUN, Dignity.RULER, Planet.MARS, Dignity.RULER)
    val m2 = MutualData(Planet.MARS, Dignity.RULER, Planet.SUN, Dignity.RULER)
    assertEquals(m1, m2)
  }
}
