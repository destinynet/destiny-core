/**
 * @author smallufo
 * Created on 2007/6/27 at 上午 3:55:35
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class ApsisTest {
  @Test
  fun testLocaleString() {
    assertEquals("近點" , Apsis.PERIHELION.toString(Locale.TAIWAN))
    assertEquals("遠點" , Apsis.APHELION.toString(Locale.TAIWAN))

    assertEquals("近点" , Apsis.PERIHELION.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("远点" , Apsis.APHELION.toString(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  fun testApsis() {
    for (each in Apsis.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.US
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])
    }
  }
}
