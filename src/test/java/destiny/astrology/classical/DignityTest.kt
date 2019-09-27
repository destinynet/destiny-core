/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 6:11:48
 */
package destiny.astrology.classical

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class DignityTest {
  @Test
  fun testDignity() {
    assertEquals("廟", Dignity.RULER.toString(Locale.TAIWAN))
    assertEquals("旺", Dignity.EXALTATION.toString(Locale.TAIWAN))
    assertEquals("陷", Dignity.DETRIMENT.toString(Locale.TAIWAN))
    assertEquals("落", Dignity.FALL.toString(Locale.TAIWAN))

    assertEquals("Ruler", Dignity.RULER.toString(Locale.US))
    assertEquals("Exaltation", Dignity.EXALTATION.toString(Locale.US))
    assertEquals("Detriment", Dignity.DETRIMENT.toString(Locale.US))
    assertEquals("Fall", Dignity.FALL.toString(Locale.US))

    for (each in Dignity.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.US
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])
    }
  }
}
