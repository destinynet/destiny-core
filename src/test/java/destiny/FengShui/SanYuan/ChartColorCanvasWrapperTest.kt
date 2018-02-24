/** 2009/12/15 上午3:51:40 by smallufo  */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test

class ChartColorCanvasWrapperTest {
  private var chart: Chart? = null
  private var wrapper: ChartColorCanvasWrapper? = null

  @Test
  fun testGetColorCanvas() {
    chart = Chart(7, Mountain.午, Symbol.兌)
    wrapper = ChartColorCanvasWrapper(chart!!)
    println(wrapper!!.colorCanvas.toString())
  }

}

