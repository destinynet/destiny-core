/**
 * @author smallufo
 * Created on 2007/6/22 at 上午 4:34:22
 */
package destiny.core.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TransPointTest {

  @Test
  fun testTransPoint() {

    assertEquals("東昇", TransPoint.RISING.getTitle(Locale.TAIWAN))
    assertEquals("西落", TransPoint.SETTING.getTitle(Locale.TAIWAN))
    assertEquals("天頂", TransPoint.MERIDIAN.getTitle(Locale.TAIWAN))
    assertEquals("天底", TransPoint.NADIR.getTitle(Locale.TAIWAN))

    assertEquals("东升", TransPoint.RISING.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("天顶", TransPoint.MERIDIAN.getTitle(Locale.SIMPLIFIED_CHINESE))

    assertEquals("Rising", TransPoint.RISING.getTitle(Locale.ENGLISH))
    assertEquals("RISING", TransPoint.RISING.toString())
  }
}
