/**
 * Created by smallufo on 2021-03-29.
 */
package destiny.core.chinese.lunarStation

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class YearEpochTest {

  @Test
  fun testToString() {
    assertEquals("EPOCH_1864" , YearEpoch.EPOCH_1864.toString())
    assertEquals("EPOCH_1564" , YearEpoch.EPOCH_1564.toString())

    assertEquals("1564年 甲子虛" , YearEpoch.EPOCH_1564.toString(Locale.TAIWAN))
    assertEquals("1564年 甲子虚" , YearEpoch.EPOCH_1564.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("1864年 甲子虛" , YearEpoch.EPOCH_1864.toString(Locale.TAIWAN))
    assertEquals("1864年 甲子虚" , YearEpoch.EPOCH_1864.toString(Locale.SIMPLIFIED_CHINESE))
  }
}
