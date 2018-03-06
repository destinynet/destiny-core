/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui.sanyuan

import kotlin.test.Test


class ChartMntContextTest {

  @Test
  fun getChartMnt() {
    val repImpl = ReplacementDefaultImpl()

    Mountain.values().forEach { mnt ->
      println("\n7運 $mnt 山 : 用替 ")
      ChartMntContext.getChartMnt(7, mnt, repImpl).also {
        println(it)
      }
    }
  }






}