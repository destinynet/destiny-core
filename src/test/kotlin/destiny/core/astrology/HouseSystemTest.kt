/**
 * @author smallufo
 * Created on 2007/6/24 at 上午 2:22:48
 */
package destiny.core.astrology

import destiny.tools.getTitle
import destiny.core.EnumTest
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class HouseSystemTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(HouseSystem::class, false)
  }

  @Test
  fun testHouseSystem() {
    assertEquals("Placidus" , HouseSystem.PLACIDUS.getTitle(Locale.TAIWAN))
    assertEquals("Placidus" , HouseSystem.PLACIDUS.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Placidus" , HouseSystem.PLACIDUS.getTitle(Locale.ENGLISH))

    for (each in HouseSystem.entries) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.getTitle(locale))
      assertNotSame('!', each.getTitle(locale)[0])
    }
  }
}
