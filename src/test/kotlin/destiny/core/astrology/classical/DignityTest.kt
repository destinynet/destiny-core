/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 6:11:48
 */
package destiny.core.astrology.classical

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class DignityTest {

  @Test
  fun testDignity() {
    assertEquals("廟", Dignity.RULER.getTitle(Locale.TAIWAN))
    assertEquals("旺", Dignity.EXALTATION.getTitle(Locale.TAIWAN))
    assertEquals("陷", Dignity.DETRIMENT.getTitle(Locale.TAIWAN))
    assertEquals("落", Dignity.FALL.getTitle(Locale.TAIWAN))

    assertEquals("庙", Dignity.RULER.getTitle(Locale.SIMPLIFIED_CHINESE))

    assertEquals("Ruler", Dignity.RULER.getTitle(Locale.US))
    assertEquals("Exaltation", Dignity.EXALTATION.getTitle(Locale.US))
    assertEquals("Detriment", Dignity.DETRIMENT.getTitle(Locale.US))
    assertEquals("Fall", Dignity.FALL.getTitle(Locale.US))

    assertEquals("RULER", Dignity.RULER.toString())

    for (each in Dignity.entries) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.US
      assertNotNull(each.getTitle(locale))
      assertNotSame('!', each.getTitle(locale)[0])
    }
  }
}
