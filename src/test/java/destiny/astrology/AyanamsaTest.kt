/**
 * @author smallufo
 * Created on 2007/12/6 at 上午 11:05:07
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class AyanamsaTest {
  @Test
  fun testAyanamsa() {
    for (each in Ayanamsa.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])
    }
  }
}
