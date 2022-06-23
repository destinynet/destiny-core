/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 3:01:26
 */
package destiny.core.astrology

import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class QualityTest {

  @Test
  fun testToString() {
    assertEquals("CARDINAL", Quality.CARDINAL.toString())
    assertEquals("FIXED", Quality.FIXED.toString())
    assertEquals("MUTABLE", Quality.MUTABLE.toString())
  }

  @Test
  fun testToStringLocale() {
    assertEquals("基本", Quality.CARDINAL.getTitle(Locale.TAIWAN))
    assertEquals("固定", Quality.FIXED.getTitle(Locale.TRADITIONAL_CHINESE))
    assertEquals("變動", Quality.MUTABLE.getTitle(Locale.TAIWAN))

    assertEquals("Cardinal", Quality.CARDINAL.getTitle(Locale.ENGLISH))
    assertEquals("Fixed", Quality.FIXED.getTitle(Locale.US))
    assertEquals("Mutable", Quality.MUTABLE.getTitle(Locale.UK))
  }

}
