/**
 * Created by smallufo on 2022-02-21.
 */
package destiny.core.chart

import kotlin.test.Test
import kotlin.test.assertEquals

internal class WidthHeightTest {

  @Test
  fun testOfPortraitPadding() {
    assertEquals((19 to 30) , WidthHeight.portraitGoldenPadding(WidthHeight.WIDTH, 100).let { (x,y) -> x.toInt() to y.toInt() })

    assertEquals((19 to 30) , WidthHeight.portraitGoldenPadding(WidthHeight.HEIGHT, 161).let { (x, y) -> x.toInt() to y.toInt() })
  }
}
