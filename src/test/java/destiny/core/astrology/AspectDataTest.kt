/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:40:44
 */
package destiny.core.astrology

import destiny.core.astrology.Planet.*
import kotlin.test.*


class AspectDataTest {

  @Test
  fun testEqual() {
    //測試 equals , 日月對調，必須仍然相等

    val data1 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 1.0)
    assertEquals("[?] [太陽, 月亮] CONJUNCTION 誤差  1.0度", data1.toString())
    val data2 = AspectData.of(MOON, SUN, Aspect.CONJUNCTION, 1.0)
    assertEquals("[?] [太陽, 月亮] CONJUNCTION 誤差  1.0度", data1.toString())


    assertEquals(data1, data2)
    assertSame(Aspect.CONJUNCTION, data1.aspect)
    assertSame(Aspect.CONJUNCTION, data2.aspect)
    assertEquals(MOON, data1.getAnotherPoint(SUN))
    assertEquals(SUN, data1.getAnotherPoint(MOON))
    assertEquals(MOON, data2.getAnotherPoint(SUN))
    assertEquals(SUN, data2.getAnotherPoint(MOON))


    val data3 = AspectData.of(SUN, MARS, Aspect.CONJUNCTION, 1.0)
    assertEquals("[?] [太陽, 火星] CONJUNCTION 誤差  1.0度", data3.toString())
    assertTrue(data1 != data3)
    assertEquals(MARS, data3.getAnotherPoint(SUN))
    assertEquals(SUN, data3.getAnotherPoint(MARS))

  }

  @Test
  fun testEqual2() {
    val data1 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, orb = 1.0, type = IAspectData.Type.APPLYING)
    assertEquals("[A] [太陽, 月亮] CONJUNCTION 誤差  1.0度", data1.toString())
    val data2 = AspectData.of(MOON, SUN, Aspect.CONJUNCTION, orb = 1.0, type = IAspectData.Type.SEPARATING)
    assertEquals("[S] [太陽, 月亮] CONJUNCTION 誤差  1.0度", data2.toString())

    assertNotEquals(data1, data2)
  }

  @Test
  fun testEqual3() {
    val data1 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, orb = 1.0, type = IAspectData.Type.APPLYING)
    val data2 = AspectData.of(MOON, SUN, Aspect.CONJUNCTION, orb = 2.0, type = IAspectData.Type.APPLYING)

    assertEquals(data1, data2)
  }

  @Test
  fun testSameStar() {
    assertFailsWith(IllegalArgumentException::class) {
      AspectData.of(SUN, SUN, Aspect.CONJUNCTION, 1.0, gmtJulDay = null)
    }

    assertFailsWith(IllegalArgumentException::class) {
      AspectData.of(SUN, SUN, Aspect.CONJUNCTION, null, 1.0, null)
    }

    assertFailsWith(IllegalArgumentException::class) {
      AspectData.of(SUN, SUN, Aspect.CONJUNCTION, null, 1.0)
    }

    assertFailsWith(IllegalArgumentException::class) {
      AspectData.of(SUN, SUN, Aspect.CONJUNCTION, IAspectData.Type.APPLYING)
    }

    assertFailsWith(IllegalArgumentException::class) {
      AspectData.of(SUN, SUN, Aspect.CONJUNCTION, 1.0)
    }

    assertFailsWith(IllegalArgumentException::class) {
      AspectData.of(MOON, MOON, Aspect.CONJUNCTION, 1.0)
    }
  }
}
