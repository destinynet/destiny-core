/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui.sanyuan

import kotlin.test.Test


class ChartMntContextTest {


  @Test
  fun test城門訣() {
    Mountain.values().forEach { mnt ->
      ChartMntContext.getChartMnt(1 , mnt).getGates().also { map ->
        println("$mnt 山 , 正 = ${map[Gate.正城門]} , 副 = ${map[Gate.副城門]}")
      }
    }

  }


  /**
   * 測試 [IChartMnt.getMntDirSpec]
   */
  @Test
  fun testGetMntDirSpec() {
    (1..9).forEach { period ->
      Mountain.values().forEach { mnt ->
        ChartMntContext.getChartMnt(period , mnt).also {
          println("$period 運 $mnt 山 : ${it.getMntDirSpec()}")
        }
      }
    }

    val repImpl = ReplacementDefaultImpl()
    (1..9).forEach { period ->
      Mountain.values().forEach { mnt ->
        ChartMntContext.getChartMnt(period , mnt , repImpl).also {
          println("[替] $period 運 $mnt 山 : ${it.getMntDirSpec()}")
        }
      }
    }
  }

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