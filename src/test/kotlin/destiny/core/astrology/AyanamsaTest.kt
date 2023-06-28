/**
 * @author smallufo
 * Created on 2007/12/6 at 上午 11:05:07
 */
package destiny.core.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class AyanamsaTest {
  @Test
  fun testAyanamsa() {
    assertEquals("Lahiri" , Ayanamsa.LAHIRI.getTitle(Locale.TAIWAN))
    assertEquals("Lahiri" , Ayanamsa.LAHIRI.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Lahiri" , Ayanamsa.LAHIRI.getTitle(Locale.ENGLISH))

    assertEquals("LAHIRI" , Ayanamsa.LAHIRI.toString())

    for (each in Ayanamsa.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.getTitle(locale))
      assertNotSame('!', each.getTitle(locale)[0])
    }
  }
}
