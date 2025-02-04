/**
 * Created by smallufo on 2023-11-20.
 */
package destiny.tools

import destiny.tools.StringTools.takeAndEllipsis
import kotlin.test.Test
import kotlin.test.assertEquals

class StringToolsTest {

  @Test
  fun testDiff() {
    assertEquals("對面的女孩", StringTools.markDifferences("對面的女孩", "對面的女孩"))
    assertEquals("對[面]的女孩", StringTools.markDifferences("對麵的女孩", "對面的女孩"))
  }

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
