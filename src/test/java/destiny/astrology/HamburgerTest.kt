/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 7:41:37
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HamburgerTest {

  @Test
  fun testHamburger() {

    //println(HamMap.map)

    var locale: Locale = Locale("zh", "TW")

    Hamburgers.array.forEach {
      assertNotNull(it)
    }
    assertEquals("Zeus", Hamburger.ZEUS.getName(locale))
    assertEquals("Ze", Hamburger.ZEUS.getAbbreviation(locale))

    locale = Locale("en")
    assertEquals("Zeus", Hamburger.ZEUS.getName(locale))
    assertEquals("Ze", Hamburger.ZEUS.getAbbreviation(locale))

  }
}
