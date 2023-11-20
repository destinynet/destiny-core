/**
 * Created by smallufo on 2023-11-20.
 */
package destiny.tools

import destiny.tools.StringTools.takeAndEllipsis
import kotlin.test.Test
import kotlin.test.assertEquals

class StringToolsTest {

  @Test
  fun testTakeAndEllipsis() {
    "1234567890".also {
      assertEquals(it, it.takeAndEllipsis(10))
    }

    "1234567890A".also {
      assertEquals("1234567890…", it.takeAndEllipsis(10))
    }
  }
}
