/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 2:51:20
 */
package destiny.core.astrology

import destiny.core.EnumTest
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class ElementTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Element::class)
  }

  @Test
  fun testToString() {
    assertEquals("火", Element.FIRE.getTitle(Locale.TAIWAN))
    assertEquals("土", Element.EARTH.getTitle(Locale.TAIWAN))
    assertEquals("風", Element.AIR.getTitle(Locale.TAIWAN))
    assertEquals("水", Element.WATER.getTitle(Locale.TAIWAN))
  }

  @Test
  fun testToLocaleString() {
    assertEquals("Fire", Element.FIRE.getTitle(Locale.US))
    assertEquals("Earth", Element.EARTH.getTitle(Locale.ENGLISH))
    assertEquals("Air", Element.AIR.getTitle(Locale.US))
    assertEquals("Water", Element.WATER.getTitle(Locale.ENGLISH))

    assertEquals("火", Element.FIRE.getTitle(Locale.TAIWAN))
    assertEquals("土", Element.EARTH.getTitle(Locale.TAIWAN))
    assertEquals("風", Element.AIR.getTitle(Locale.TRADITIONAL_CHINESE))
    assertEquals("水", Element.WATER.getTitle(Locale.TRADITIONAL_CHINESE))
  }
}
