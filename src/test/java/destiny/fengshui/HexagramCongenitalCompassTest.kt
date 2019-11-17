/**
 * Created by smallufo on 2019-11-17.
 */
package destiny.fengshui

import destiny.iching.Hexagram
import kotlin.test.Test
import kotlin.test.assertEquals


class HexagramCongenitalCompassTest {

  private val compass = HexagramCongenitalCompass()

  @Test
  fun testDegree() {

    compass.getStartDegree(Hexagram.復).also {
      assertEquals(0.0, it)
    }

    compass.getStartDegree(Hexagram.頤).also {
      assertEquals(5.625, it)
    }

    compass.getStartDegree(Hexagram.臨).also {
      assertEquals(90.0, it)
    }

    compass.getStartDegree(Hexagram.乾).also {
      assertEquals(180 - 5.625, it)
    }

    compass.getStartDegree(Hexagram.姤).also {
      assertEquals(180.0, it)
    }

    compass.getStartDegree(Hexagram.遯).also {
      assertEquals(270.0, it)
    }

    compass.getStartDegree(Hexagram.坤).also {
      assertEquals(360 - 5.625, it)
    }
    compass.getEndDegree(Hexagram.坤).also {
      assertEquals(360.0, it)
    }
  }
}
