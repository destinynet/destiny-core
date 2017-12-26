/**
 * @author smallufo
 * Created on 2007/6/24 at 上午 2:22:48
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class HouseSystemTest {
  @Test
  fun testHouseSystem() {
    for (each in HouseSystem.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])
    }
  }
}
