/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test

class ChartCanvasFullTest {

  @Test
  fun `七運，午山子向 , 坎底`() {
    val chart = Chart(7, Mountain.午, Symbol.坎)
    val canvas = ChartCanvasFull(chart)
    println(canvas.toString())
  }

  @Test
  fun `七運，午山子向 , 兌底`() {
    val chart = Chart(7, Mountain.午, Symbol.兌)
    val canvas = ChartCanvasFull(chart)
    println(canvas.toString())
  }
}