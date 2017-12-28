/**
 * @author smallufo
 * Created on 2008/1/27 at 上午 2:38:44
 */
package destiny.core.calendar.eightwords

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class HemisphereByTest {
  @Test
  fun testHemisphereBy() {
    for (each in HemisphereBy.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])

    }
  }
}
