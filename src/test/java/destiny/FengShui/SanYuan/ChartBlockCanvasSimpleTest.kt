/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertNotNull

class ChartBlockCanvasSimpleTest {

  @Test
  fun render() {
    val cb = ChartBlock(Symbol.坎, 1, 2, 3)
    val canvas = ChartBlockCanvasSimple(cb)
    println(canvas)
    assertNotNull(canvas.toString())
  }

  @Test
  fun `中間為空`() {
    val cb = ChartBlock(null, 1, 2, 3)
    val canvas = ChartBlockCanvasSimple(cb)
    println(canvas)
    assertNotNull(canvas.toString())
  }
}