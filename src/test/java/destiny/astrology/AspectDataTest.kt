/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:40:44
 */
package destiny.astrology

import destiny.astrology.Planet.*
import kotlin.test.*


class AspectDataTest {

  @Test
  fun testEqual() {
    //測試 equals , 日月對調，必須仍然相等

    val data1 = AspectData(SUN, MOON, Aspect.CONJUNCTION, 1.0)
    val data2 = AspectData(MOON, SUN, Aspect.CONJUNCTION, 1.0)


    assertEquals(data1, data2)
    assertEquals(MOON, data1.getAnotherPoint(SUN))
    assertEquals(SUN, data1.getAnotherPoint(MOON))
    assertEquals(MOON, data2.getAnotherPoint(SUN))
    assertEquals(SUN, data2.getAnotherPoint(MOON))


    val data3 = AspectData(SUN, MARS, Aspect.CONJUNCTION, 1.0)
    assertTrue(data1 != data3)
    assertEquals(MARS , data3.getAnotherPoint(SUN))
    assertEquals(SUN , data3.getAnotherPoint(MARS))

    println(data1)
    println(data2)
    println(data3)
  }

  @Test
  fun testEqual2() {
    val data1 = AspectData(SUN, MOON, Aspect.CONJUNCTION, 1.0 , type = AspectData.Type.APPLYING)
    val data2 = AspectData(MOON, SUN, Aspect.CONJUNCTION, 1.0 , type = AspectData.Type.SEPARATING)

    assertNotEquals(data1 , data2)
  }

  @Test
  fun testEqual3() {
    val data1 = AspectData(SUN, MOON, Aspect.CONJUNCTION, 1.0 , type = AspectData.Type.APPLYING)
    val data2 = AspectData(MOON, SUN, Aspect.CONJUNCTION, 2.0 , type = AspectData.Type.APPLYING)

    assertEquals(data1 , data2)
  }

  @Test
  fun testSameStar() {
    val data1 = AspectData(SUN, SUN, Aspect.CONJUNCTION, 1.0)
    val data2 = AspectData(MOON , MOON, Aspect.CONJUNCTION, 1.0)
    assertNotEquals(data1 , data2)

    assertNull(data1.getAnotherPoint(SUN))
    assertNull(data1.getAnotherPoint(MOON))
    assertNull(data1.getAnotherPoint(MARS))
  }
}
