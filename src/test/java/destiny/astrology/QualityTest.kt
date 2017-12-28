/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 3:01:26
 */
package destiny.astrology

import kotlin.test.Test

import java.util.Locale
import kotlin.test.assertEquals


class QualityTest {

  @Test
  fun testToString() {
    assertEquals("基本", Quality.CARDINAL.toString())
    assertEquals("固定", Quality.FIXED.toString())
    assertEquals("變動", Quality.MUTABLE.toString())
  }

  @Test
  fun testToStringLocale() {
    assertEquals("基本", Quality.CARDINAL.toString(Locale.TAIWAN))
    assertEquals("固定", Quality.FIXED.toString(Locale.TRADITIONAL_CHINESE))
    assertEquals("變動", Quality.MUTABLE.toString(Locale.TAIWAN))

    assertEquals("Cardinal", Quality.CARDINAL.toString(Locale.ENGLISH))
    assertEquals("Fixed", Quality.FIXED.toString(Locale.US))
    assertEquals("Mutable", Quality.MUTABLE.toString(Locale.UK))
  }

}
