/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.Mountain
import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import destiny.core.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class ChartMntContextTest {

  private val replaceImpl = ReplacementDefaultImpl()

  @Test
  fun test城門訣() {
    // 四運 , 子山午向 , 四運子山午向的向方兩旁，均有城門可取。此兩旁的城門，正好補救了午向沒有旺星到向之不足。
    ChartMntContext.getChartMnt(4.toPeriod(), Mountain.子).getGates().also {
      assertEquals(mapOf(
        Gate.正城門 to Pair(Mountain.巽, true), Gate.副城門 to Pair(
        Mountain.坤, true)), it)
    }

    // 七運 , 酉山卯向 , 只有正城門可取；副城門不可取
    ChartMntContext.getChartMnt(7.toPeriod(), Mountain.酉).getGates().also {
      assertEquals(mapOf(
        Gate.正城門 to Pair(Mountain.艮, true), Gate.副城門 to Pair(
        Mountain.巽, false)), it)
    }

    // 一運 , 午山子向 , 正副皆不可取
    ChartMntContext.getChartMnt(1.toPeriod(), Mountain.午).getGates().also {
      assertEquals(mapOf(
        Gate.正城門 to Pair(Mountain.乾, false), Gate.副城門 to Pair(
        Mountain.艮, false)), it)
    }
  }

  /**
   * 一運 24 山的城門訣：每山都有正、副兩個城門，各自標記此運是否可取。
   * 原本只是把 24 行 println 出來，沒有任何斷言。
   */
  @Test
  fun 一運所有山之城門訣() {
    val all = Mountain.entries.associateWith { ChartMntContext.getChartMnt(1.toPeriod(), it).getGates() }

    all.forEach { (mnt, gates) ->
      assertEquals(setOf(Gate.正城門, Gate.副城門), gates.keys, "$mnt 山")
    }

    // 抽驗：子山正副皆可取、丑山正副皆不可取
    assertEquals(
      mapOf(Gate.正城門 to (Mountain.巽 to true), Gate.副城門 to (Mountain.坤 to true)),
      all.getValue(Mountain.子)
    )
    assertEquals(
      mapOf(Gate.正城門 to (Mountain.庚 to false), Gate.副城門 to (Mountain.丙 to false)),
      all.getValue(Mountain.丑)
    )

    // 一運 48 個城門中恰有一半可取
    assertEquals(24, all.values.sumOf { gates -> gates.values.count { it.second } })
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
    ChartMntContext.getChartMnt(8.toPeriod(), Mountain.子).also { chart ->
      assertEquals(ChartBlock.of(null, 4, 3, 8), chart.getCenterBlock())
      assertEquals(ChartBlock.of(null, 4, 3, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock.of(Symbol.乾, 5, 2, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock.of(Symbol.兌, 6, 1, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock.of(Symbol.艮, 7, 9, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock.of(Symbol.離, 8, 8, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock.of(Symbol.坎, 9, 7, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock.of(Symbol.坤, 1, 6, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock.of(Symbol.震, 2, 5, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock.of(Symbol.巽, 3, 4, 7), chart.getChartBlockFromSymbol(Symbol.巽))
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
    ChartMntContext.getChartMnt(8.toPeriod(), Mountain.子, replaceImpl).also { chart ->
      assertEquals(ChartBlock.of(null, 6, 2, 8), chart.getCenterBlock())
      assertEquals(ChartBlock.of(null, 6, 2, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock.of(Symbol.乾, 7, 1, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock.of(Symbol.兌, 8, 9, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock.of(Symbol.艮, 9, 8, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock.of(Symbol.離, 1, 7, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock.of(Symbol.坎, 2, 6, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock.of(Symbol.坤, 3, 5, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock.of(Symbol.震, 4, 4, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock.of(Symbol.巽, 5, 3, 7), chart.getChartBlockFromSymbol(Symbol.巽))
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
    ChartMntContext.getChartMnt(8.toPeriod(), Mountain.申).also { chart ->
      assertEquals(ChartBlock.of(null, 5, 2, 8), chart.getCenterBlock())
      assertEquals(ChartBlock.of(null, 5, 2, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock.of(Symbol.乾, 6, 3, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock.of(Symbol.兌, 7, 4, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock.of(Symbol.艮, 8, 5, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock.of(Symbol.離, 9, 6, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock.of(Symbol.坎, 1, 7, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock.of(Symbol.坤, 2, 8, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock.of(Symbol.震, 3, 9, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock.of(Symbol.巽, 4, 1, 7), chart.getChartBlockFromSymbol(Symbol.巽))
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
    ChartMntContext.getChartMnt(8.toPeriod(), Mountain.申, replaceImpl).also { chart ->
      assertEquals(ChartBlock.of(null, 5, 1, 8), chart.getCenterBlock())
      assertEquals(ChartBlock.of(null, 5, 1, 8), chart.getChartBlockFromSymbol(null))
      assertEquals(ChartBlock.of(Symbol.乾, 6, 2, 9), chart.getChartBlockFromSymbol(Symbol.乾))
      assertEquals(ChartBlock.of(Symbol.兌, 7, 3, 1), chart.getChartBlockFromSymbol(Symbol.兌))
      assertEquals(ChartBlock.of(Symbol.艮, 8, 4, 2), chart.getChartBlockFromSymbol(Symbol.艮))
      assertEquals(ChartBlock.of(Symbol.離, 9, 5, 3), chart.getChartBlockFromSymbol(Symbol.離))
      assertEquals(ChartBlock.of(Symbol.坎, 1, 6, 4), chart.getChartBlockFromSymbol(Symbol.坎))
      assertEquals(ChartBlock.of(Symbol.坤, 2, 7, 5), chart.getChartBlockFromSymbol(Symbol.坤))
      assertEquals(ChartBlock.of(Symbol.震, 3, 8, 6), chart.getChartBlockFromSymbol(Symbol.震))
      assertEquals(ChartBlock.of(Symbol.巽, 4, 9, 7), chart.getChartBlockFromSymbol(Symbol.巽))
    }
  }

  /**
   * [IChartMnt.getMntDirSpec] 的契約（見其原始碼註解）：
   * **正常的挨星下卦一定有值，只有替星盤才可能為 null。**
   *
   * 9 運 × 24 山 = 216 種下卦盤全部有值，且只會落在 [MntDirSpec] 的四種格局裡。
   * 原本這裡是把 216 + 216 行結果 println 出來 —— 包含替星盤那一大片 `null`，
   * 沒有任何斷言，看起來像「都有跑到」，其實什麼都沒驗。
   */
  @Test
  fun testGetMntDirSpec() {
    val downGua: List<MntDirSpec?> = (1..9).flatMap { period ->
      Mountain.entries.map { mnt ->
        ChartMntContext.getChartMnt(period.toPeriod(), mnt).getMntDirSpec()
      }
    }

    assertEquals(216, downGua.size)
    assertTrue(downGua.all { it != null }, "下卦盤不該出現 null")
    assertEquals(MntDirSpec.entries.toSet(), downGua.toSet(), "四種格局都該出現")

    // 抽驗兩個定盤
    assertEquals(MntDirSpec.雙星到向, ChartMntContext.getChartMnt(7.toPeriod(), Mountain.午).getMntDirSpec())
    assertEquals(MntDirSpec.雙星到山, ChartMntContext.getChartMnt(7.toPeriod(), Mountain.子).getMntDirSpec())
  }

  /**
   * 替星盤（用替）就允許 null —— 這正是 [IChartMnt.getMntDirSpec] 回傳型別可為 null 的唯一理由。
   *
   * 216 種當中有 142 種取不到格局。這個數字是**現況的刻畫**，並非某條命理規則的推論；
   * 釘住它是為了讓「替星規則被動到」這件事看得見（原本這一大片 null 只是被 println 沖掉）。
   */
  @Test
  fun `替星盤的山向格局可以是 null`() {
    val replaced: List<MntDirSpec?> = (1..9).flatMap { period ->
      Mountain.entries.map { mnt ->
        ChartMntContext.getChartMnt(period.toPeriod(), mnt, replaceImpl).getMntDirSpec()
      }
    }

    assertEquals(216, replaced.size)
    assertEquals(142, replaced.count { it == null })
    assertEquals(MntDirSpec.entries.toSet(), replaced.filterNotNull().toSet())
  }

  /** 用替與否，盤面結構不變：9 宮（8 卦 + 中宮），且座山、運數如實帶入 */
  @Test
  fun getChartMnt() {
    Mountain.entries.forEach { mnt ->
      listOf(false to ChartMntContext.getChartMnt(7.toPeriod(), mnt),
             true to ChartMntContext.getChartMnt(7.toPeriod(), mnt, replaceImpl)
      ).forEach { (replacement, chart) ->
        assertEquals(9, chart.blocks.size, "$mnt 山 (替=$replacement)")
        assertEquals(7.toPeriod(), chart.period)
        assertEquals(mnt, chart.mnt)
        assertEquals(replacement, chart.replacement)
        // 8 卦各一宮 + 中宮（symbol 為 null）
        assertEquals(Symbol.entries.toSet(), chart.blocks.mapNotNull { it.symbol }.toSet())
        assertEquals(1, chart.blocks.count { it.symbol == null })
      }
    }
  }


}
