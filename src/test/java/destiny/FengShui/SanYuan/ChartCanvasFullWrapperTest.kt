/** 2009/12/15 上午3:51:40 by smallufo  */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test

class ChartCanvasFullWrapperTest {

  @Test
  fun testGetColorCanvas() {
    val chart = Chart(7, Mountain.午, Symbol.兌)
    val wrapper = ChartCanvasFullWrapper(chart)
    println(wrapper.colorCanvas.toString())
  }

}

