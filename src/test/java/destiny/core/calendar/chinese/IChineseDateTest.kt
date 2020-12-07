package destiny.core.calendar.chinese

import kotlin.test.Test
import kotlin.test.assertSame

class IChineseDateTest {

  @Test
  fun testCycles() {
    assertSame(77, IChineseDate.getCycleOfYear(1983))
    assertSame(78, IChineseDate.getCycleOfYear(1984))
  }
}
