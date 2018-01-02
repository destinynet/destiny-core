/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 2:51:20
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class ElementTest {
  @Test
  fun testToString() {
    assertEquals("火", Element.FIRE.toString())
    assertEquals("土", Element.EARTH.toString())
    assertEquals("風", Element.AIR.toString())
    assertEquals("水", Element.WATER.toString())
  }

  @Test
  fun testToLocaleString() {
    assertEquals("Fire", Element.FIRE.toString(Locale.US))
    assertEquals("Earth", Element.EARTH.toString(Locale.ENGLISH))
    assertEquals("Air", Element.AIR.toString(Locale.US))
    assertEquals("Water", Element.WATER.toString(Locale.ENGLISH))

    assertEquals("火", Element.FIRE.toString(Locale.TAIWAN))
    assertEquals("土", Element.EARTH.toString(Locale.TAIWAN))
    assertEquals("風", Element.AIR.toString(Locale.TRADITIONAL_CHINESE))
    assertEquals("水", Element.WATER.toString(Locale.TRADITIONAL_CHINESE))
  }
}
