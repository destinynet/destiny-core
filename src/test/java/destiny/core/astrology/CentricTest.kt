/**
 * @author smallufo
 * Created on 2007/12/10 at 上午 10:36:13
 */
package destiny.core.astrology

import destiny.core.EnumTest
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class CentricTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Centric::class)
  }

  @Test
  fun testCentric() {
    assertEquals("地表", Centric.TOPO.getTitle(Locale.TAIWAN))
    assertEquals("日心", Centric.HELIO.getTitle(Locale.TAIWAN))
    assertEquals("質心", Centric.BARY.getTitle(Locale.TAIWAN))

    assertEquals("TopoCentric", Centric.TOPO.getTitle(Locale.ENGLISH))

    for (each in Centric.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.getTitle(locale))
      assertNotSame('!', each.getTitle(locale)[0])
    }
  }
}
