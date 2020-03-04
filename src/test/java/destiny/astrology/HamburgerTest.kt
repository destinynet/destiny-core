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

    Hamburger.array.forEach {
      assertNotNull(it)
    }
    assertEquals("Zeus", Hamburger.ZEUS.toString(Locale("zh", "TW")))
    //assertEquals("Ze", Hamburger.ZEUS.getAbbreviation(Locale("zh", "TW")))

    val locale = Locale("en")
    assertEquals("Zeus", Hamburger.ZEUS.toString(locale))
    assertEquals("Ze", Hamburger.ZEUS.getAbbreviation(locale))

  }
}
