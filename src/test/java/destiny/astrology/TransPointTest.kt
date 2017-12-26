/**
 * @author smallufo
 * Created on 2007/6/22 at 上午 4:34:22
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TransPointTest {
  @Test
  fun testTransPoint() {

    assertEquals("東昇", TransPoint.RISING.toString())

    assertEquals("東昇", TransPoint.RISING.toString())
    assertEquals("西落", TransPoint.SETTING.toString())
    assertEquals("天頂", TransPoint.MERIDIAN.toString())
    assertEquals("天底", TransPoint.NADIR.toString())

    val locale: Locale = Locale.ENGLISH
    assertEquals("Rising", TransPoint.RISING.toString(locale))
  }
}
