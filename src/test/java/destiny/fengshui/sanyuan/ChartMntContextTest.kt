/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui.sanyuan

import kotlin.test.Test
import kotlin.test.assertEquals


class ChartMntContextTest {



  @Test
  fun test城門訣() {

    // 四運 , 子山午向 , 四運子山午向的向方兩旁，均有城門可取。此兩旁的城門，正好補救了午向沒有旺星到向之不足。
    ChartMntContext.getChartMnt(4 , Mountain.子).getGates().also {
      assertEquals(mapOf(Gate.正城門 to Pair(Mountain.巽 , true) , Gate.副城門 to Pair(Mountain.坤 , true) ) , it)
    }

    // 七運 , 酉山卯向 , 只有正城門可取；副城門不可取
    ChartMntContext.getChartMnt(7 , Mountain.酉).getGates().also {
      assertEquals(mapOf(Gate.正城門 to Pair(Mountain.艮 , true) , Gate.副城門 to Pair(Mountain.巽 , false) ) , it)
    }

    // 一運 , 午山子向 , 正副皆不可取
    ChartMntContext.getChartMnt(1 , Mountain.午).getGates().also {
      assertEquals(mapOf(Gate.正城門 to Pair(Mountain.乾 , false) , Gate.副城門 to Pair(Mountain.艮 , false) ) , it)
    }



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