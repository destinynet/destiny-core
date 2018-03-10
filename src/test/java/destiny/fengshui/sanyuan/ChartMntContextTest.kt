/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals


class ChartMntContextTest {

  val replaceImpl = ReplacementDefaultImpl()

  @Test
  fun test城門訣() {

    // 四運 , 子山午向 , 四運子山午向的向方兩旁，均有城門可取。此兩旁的城門，正好補救了午向沒有旺星到向之不足。
    ChartMntContext.getChartMnt(4, Mountain.子).getGates().also {
      assertEquals(mapOf(Gate.正城門 to Pair(Mountain.巽, true), Gate.副城門 to Pair(Mountain.坤, true)), it)
    }

    // 七運 , 酉山卯向 , 只有正城門可取；副城門不可取
    ChartMntContext.getChartMnt(7, Mountain.酉).getGates().also {
      assertEquals(mapOf(Gate.正城門 to Pair(Mountain.艮, true), Gate.副城門 to Pair(Mountain.巽, false)), it)
    }

    // 一運 , 午山子向 , 正副皆不可取
    ChartMntContext.getChartMnt(1, Mountain.午).getGates().also {
      assertEquals(mapOf(Gate.正城門 to Pair(Mountain.乾, false), Gate.副城門 to Pair(Mountain.艮, false)), it)
    }



    Mountain.values().forEach { mnt ->
      ChartMntContext.getChartMnt(1, mnt).getGates().also { map ->
        println("$mnt 山 , 正 = ${map[Gate.正城門]} , 副 = ${map[Gate.副城門]}")
      }
    }

  }

  /**
   * 八運 子山午向
   *
  ３４　８８　１６
  巽七　離三　坤五
  　　　　　　　　
  ２５　４３　６１
  震六　中八　兌一
  　　　　　　　　
  ７９　９７　５２
  艮二　坎四　乾九
   */
  @Test
  fun `八運 子山午向`() {
    ChartMntContext.getChartMnt(8, Mountain.子).also { chart ->
      assertEquals(ChartBlock(null, 4, 3, 8), chart.getCenterBlock())
      assertEquals(ChartBlock(null, 4, 3, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock(Symbol.乾, 5, 2, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock(Symbol.兌, 6, 1, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock(Symbol.艮, 7, 9, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock(Symbol.離, 8, 8, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock(Symbol.坎, 9, 7, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock(Symbol.坤, 1, 6, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock(Symbol.震, 2, 5, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock(Symbol.巽, 3, 4, 7), chart.getChartBlockFromSymbol(Symbol.巽))
    }
  }


  /**
   *
   * 承上
   *
   * 八運 子山午向 , 用替

  ５３　１７　３５
  巽七　離三　坤五
  　　　　　　　　
  ４４　６２　８９
  震六　中八　兌一
  　　　　　　　　
  ９８　２６　７１
  艮二　坎四　乾九
   */
  @Test
  fun `八運 子山午向 , 用替`() {
    ChartMntContext.getChartMnt(8, Mountain.子 , replaceImpl).also { chart ->
      assertEquals(ChartBlock(null, 6, 2, 8), chart.getCenterBlock())
      assertEquals(ChartBlock(null, 6, 2, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock(Symbol.乾, 7, 1, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock(Symbol.兌, 8, 9, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock(Symbol.艮, 9, 8, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock(Symbol.離, 1, 7, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock(Symbol.坎, 2, 6, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock(Symbol.坤, 3, 5, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock(Symbol.震, 4, 4, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock(Symbol.巽, 5, 3, 7), chart.getChartBlockFromSymbol(Symbol.巽))
    }
  }


  /**
   * 八運 申山寅向 , 五入中
   *
  ４１　９６　２８
  巽七　離三　坤五
  　　　　　　　　
  ３９　５２　７４
  震六　中八　兌一
  　　　　　　　　
  ８５　１７　６３
  艮二　坎四　乾九
   */
  @Test
  fun `八運 申山寅向`() {
    ChartMntContext.getChartMnt(8, Mountain.申).also { chart ->
      assertEquals(ChartBlock(null, 5, 2, 8), chart.getCenterBlock())
      assertEquals(ChartBlock(null, 5, 2, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock(Symbol.乾, 6, 3, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock(Symbol.兌, 7, 4, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock(Symbol.艮, 8, 5, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock(Symbol.離, 9, 6, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock(Symbol.坎, 1, 7, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock(Symbol.坤, 2, 8, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock(Symbol.震, 3, 9, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock(Symbol.巽, 4, 1, 7), chart.getChartBlockFromSymbol(Symbol.巽))
    }
  }


  /**
   * 承上
   * 八運 申山寅向 , 五入中 , 用替
   *
  ４９　９５　２７
  巽七　離三　坤五
  　　　　　　　　
  ３８　５１　７３
  震六　中八　兌一
  　　　　　　　　
  ８４　１６　６２
  艮二　坎四　乾九
   */
  @Test
  fun `八運 申山寅向 用替`() {
    ChartMntContext.getChartMnt(8, Mountain.申, replaceImpl).also { chart ->
      assertEquals(ChartBlock(null, 5, 1, 8), chart.getCenterBlock())
      assertEquals(ChartBlock(null, 5, 1, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock(Symbol.乾, 6, 2, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock(Symbol.兌, 7, 3, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock(Symbol.艮, 8, 4, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock(Symbol.離, 9, 5, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock(Symbol.坎, 1, 6, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock(Symbol.坤, 2, 7, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock(Symbol.震, 3, 8, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock(Symbol.巽, 4, 9, 7), chart.getChartBlockFromSymbol(Symbol.巽))
    }
  }

  /**
   * 測試 [IChartMnt.getMntDirSpec]
   */
  @Test
  fun testGetMntDirSpec() {
    (1..9).forEach { period ->
      Mountain.values().forEach { mnt ->
        ChartMntContext.getChartMnt(period, mnt).also {
          println("$period 運 $mnt 山 : ${it.getMntDirSpec()}")
        }
      }
    }

    val repImpl = ReplacementDefaultImpl()
    (1..9).forEach { period ->
      Mountain.values().forEach { mnt ->
        ChartMntContext.getChartMnt(period, mnt, repImpl).also {
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