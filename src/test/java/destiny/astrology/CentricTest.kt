/**
 * @author smallufo
 * Created on 2007/12/10 at 上午 10:36:13
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class CentricTest {
  @Test
  fun testCentric() {
    assertEquals("地表", Centric.TOPO.toString(Locale.TAIWAN))
    assertEquals("日心", Centric.HELIO.toString(Locale.TAIWAN))
    assertEquals("質心", Centric.BARY.toString(Locale.TAIWAN))

    for (each in Centric.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])
    }
  }
}
