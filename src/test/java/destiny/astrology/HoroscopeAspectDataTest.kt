/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:40:44
 */
package destiny.astrology

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class HoroscopeAspectDataTest {
  @Test
  fun testHoroscopeAspectData() {
    //測試 equals , 日月對調，必須仍然相等
    val data1 = HoroscopeAspectData(Planet.SUN, Planet.MOON, Aspect.CONJUNCTION, 1.0)
    val data2 = HoroscopeAspectData(Planet.MOON, Planet.SUN, Aspect.CONJUNCTION, 1.0)

    assertEquals(data1, data2)

    val data3 = HoroscopeAspectData(Planet.SUN, Planet.MARS, Aspect.CONJUNCTION, 1.0)
    assertTrue(data1 != data3)
  }

}
