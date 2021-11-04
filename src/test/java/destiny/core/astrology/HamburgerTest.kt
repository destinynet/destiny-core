/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 7:41:37
 */
package destiny.core.astrology

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class HamburgerTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testHamburger() {

    Hamburger.values.forEach {
      assertNotNull(it)
    }
    assertEquals("Zeus", Hamburger.ZEUS.toString(Locale("zh", "TW")))
    //assertEquals("Ze", Hamburger.ZEUS.getAbbreviation(Locale("zh", "TW")))

    val locale = Locale("en")
    assertEquals("Zeus", Hamburger.ZEUS.toString(locale))
    assertEquals("Ze", Hamburger.ZEUS.getAbbreviation(locale))
  }

  @Test
  fun testStringConvert() {
    Hamburger.values.forEach { star ->
      logger.info { "$star = ${star.toString(Locale.ENGLISH)}" }
      assertSame(star, Hamburger.fromString(star.toString(Locale.ENGLISH)))
    }
  }
}
