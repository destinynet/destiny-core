/**
 * @author smallufo
 * @date 2002/9/23
 * @time 上午 11:21:27
 */
package destiny.fengshui.sanyuan

import org.junit.Assert.assertEquals
import kotlin.test.Test
import kotlin.test.assertSame

class EarthlyCompassTest {

  @Test
  fun testEarthlyCompass() {
    val compass = EarthlyCompass()

    //子山開始度數 (352.5)
    assertEquals(352.5, compass.getStartDegree(Mountain.子), 0.0)
    assertEquals(7.5, compass.getEndDegree(Mountain.子), 0.0)

    //卯山開始度數 ( 82.5)
    assertEquals(82.5, compass.getStartDegree(Mountain.卯), 0.0)
    assertEquals(97.5, compass.getEndDegree(Mountain.卯), 0.0)

    //癸山開始度數 (  7.5)
    assertEquals(7.5, compass.getStartDegree(Mountain.癸), 0.0)
    assertEquals(22.5, compass.getEndDegree(Mountain.癸), 0.0)

    //午山開始度數 (172.5)
    assertEquals(172.5, compass.getStartDegree(Mountain.午), 0.0)
    assertEquals(187.5, compass.getEndDegree(Mountain.午), 0.0)

    //乾山開始度數 (307.5)
    assertEquals(307.5, compass.getStartDegree(Mountain.乾), 0.0)
    assertEquals(322.5, compass.getEndDegree(Mountain.乾), 0.0)


    //359度是屬於 (子)
    assertSame(Mountain.子, compass.getMnt(359.0))
    //  0度是屬於 (子)
    assertSame(Mountain.子, compass.getMnt(0.0))
    //  9度是屬於 (癸)
    assertSame(Mountain.癸, compass.getMnt(9.0))
    //128度是屬於 (巽)
    assertSame(Mountain.巽, compass.getMnt(128.0))
    //325度是屬於 (亥)
    assertSame(Mountain.亥, compass.getMnt(325.0))

    assertSame(false, compass.getYinYang(Mountain.子))
    assertSame(false, compass.getYinYang(Mountain.丑))
    //寅(陽)
    assertSame(true, compass.getYinYang(Mountain.寅))
    assertSame(false, compass.getYinYang(Mountain.卯))
    assertSame(false, compass.getYinYang(Mountain.辰))
    //巳(陽)
    assertSame(true, compass.getYinYang(Mountain.巳))
    assertSame(false, compass.getYinYang(Mountain.午))
    assertSame(false, compass.getYinYang(Mountain.未))


    assertSame(true, compass.getYinYang(Mountain.乾))
    assertSame(true, compass.getYinYang(Mountain.坤))
    assertSame(true, compass.getYinYang(Mountain.艮))
    assertSame(true, compass.getYinYang(Mountain.巽))

    assertSame(true, compass.getYinYang(Mountain.甲))
    assertSame(false, compass.getYinYang(Mountain.乙))
    assertSame(true, compass.getYinYang(Mountain.丙))
    assertSame(false, compass.getYinYang(Mountain.丁))
    assertSame(true, compass.getYinYang(Mountain.庚))
    assertSame(false, compass.getYinYang(Mountain.辛))
    assertSame(true, compass.getYinYang(Mountain.壬))
    assertSame(false, compass.getYinYang(Mountain.癸))
  }
}
