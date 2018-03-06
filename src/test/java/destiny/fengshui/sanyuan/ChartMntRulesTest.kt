/**
 * Created by smallufo on 2018-03-06.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ChartMntRulesTest {

  /**
   * 全局合十
   * 範例：
   * 七運，子山午向 , 山盤 與 天盤 相加 均為 10
  ４１　８６　６８
  巽六　離二　坤四
  　　　　　　　　
  ５９　３２　１４
  震五　中七　兌九
  　　　　　　　　
  ９５　７７　２３
  艮一　坎三　乾八
   * */
  @Test
  fun 全局合十() {
    ChartMntContext.getChartMnt(7, Mountain.子).also {
      assertEquals(ChartRule.合十(MntDir.山), ChartMntRules.match10(it))
    }

    // 印出全部 (共24局)
    (1..9).forEach { period ->
      Mountain.values().forEach { mnt ->
        ChartMntContext.getChartMnt(period, mnt).let { chart ->
          ChartMntRules.match10(chart).let { rule ->
            rule?.let {
              println("合十 : $period 運 , $mnt 山 ${mnt.opposite} 向 ")
            }
          }
        }
      }
    }
  }


  /**
   * 已知：
   * 7運 , 庚山 甲向 , 向盤分佈 呈現與洛書相同 , 故 , 全局伏吟(元旦盤)

  ８４　４９　６２
  巽六　離二　坤四
  　　　　　　　　
  ７３　９５　２７
  震五　中七　兌九
  　　　　　　　　
  ３８　５１　１６
  艮一　坎三　乾八
   */
  @Test
  fun 全局伏吟_元旦盤() {

    ChartMntContext.getChartMnt(7, Mountain.庚).also {
      assertEquals(ChartRule.伏吟元旦盤(MntDir.向), ChartMntRules.beneathSameOrigin(it))
    }

    // 印出全部 (共24局)
    (1..9).forEach { period ->
      Mountain.values().forEach { mnt ->
        ChartMntContext.getChartMnt(period, mnt).let { chart ->
          ChartMntRules.beneathSameOrigin(chart)?.let {
            println("伏吟 : $period 運 , $mnt 山 ${mnt.opposite} 向 : ${it.mntDir}盤 伏吟")
          }
        }
      }
    }
  }

  /**
   * 已知
   *
  七運，卯山酉向 , 山盤 5入中 , 逆飛 , 形成 山盤與洛書盤對沖

  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八
   */
  @Test
  fun 全局反吟() {

    ChartMntContext.getChartMnt(7, Mountain.卯).also {
      assertEquals(ChartRule.反吟(MntDir.山), ChartMntRules.reversed(it))
    }

    // 印出全部 (共24局)
    (1..9).forEach { period ->
      Mountain.values().forEach { mnt ->
        ChartMntContext.getChartMnt(period, mnt).let { chart ->
          ChartMntRules.reversed(chart).let { rule ->
            rule?.let {
              println("反吟 : $period 運 , $mnt 山 ${mnt.opposite} 向 , ${it.mntDir}星 全局反吟")
            }
          }
        }
      }
    }
  }


  /**
   * 七運，卯山酉向

  |--山盤 伏吟天盤
  V
  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７ <-- 7 即為 兌宮洛書數， [BlockRule.伏吟元旦盤]
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八  <-- 向盤 伏吟天盤

   */
  @Test
  fun 單宮伏吟元旦盤() {
    ChartMntContext.getChartMnt(7, Mountain.卯).also {
      assertEquals(BlockRule.伏吟元旦盤(MntDir.向), ChartMntRules.beneathSameOrigin(it.getChartBlockFromSymbol(Symbol.兌)))
    }
  }


  /**
   * 七運，卯山酉向

  |--山盤 伏吟天盤 [BlockRule.伏吟天盤]
  V
  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７ <-- 7 即為 兌宮洛書數
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八  <-- 向盤 伏吟天盤 [BlockRule.伏吟天盤]

   */
  @Test
  fun 單宮伏吟天盤() {
    ChartMntContext.getChartMnt(7, Mountain.卯).also {
      assertEquals(BlockRule.伏吟天盤(MntDir.向), ChartMntRules.beneathSameSky(it.getChartBlockFromSymbol(Symbol.乾)))
      assertEquals(BlockRule.伏吟天盤(MntDir.山), ChartMntRules.beneathSameSky(it.getChartBlockFromSymbol(Symbol.巽)))
    }

    // 另一範例 : 一運之艮山坤向，艮方向星與運星伏吟，發生 於 坐向二宮。
    ChartMntContext.getChartMnt(1, Mountain.艮).also {
      assertEquals(BlockRule.伏吟天盤(MntDir.向), ChartMntRules.beneathSameSky(it.getChartBlockFromSymbol(Symbol.艮)))
    }

    // 另一範例 : 二運之子山午向，震宮之山星與運星伏吟，發生於非坐向二宮。
    ChartMntContext.getChartMnt(2, Mountain.子).also {
      assertEquals(BlockRule.伏吟天盤(MntDir.山), ChartMntRules.beneathSameSky(it.getChartBlockFromSymbol(Symbol.震)))
    }
  }

  /**
  八運，卯山酉向
   *
  　　　|-- 山星 1 + 離(9) = 10 , 山盤 反吟
  　　　v
  ５２　１６　３４
  巽七　離三　坤五
  　　　　　　　　
  ４３　６１　８８
  震六　中八　兌一
  　　　　　　　　
  ９７　２５　７９
  艮二　坎四　乾九

   */
  @Test
  fun `單宮反吟(元旦盤)`() {
    ChartMntContext.getChartMnt(8, Mountain.卯).also {
      assertEquals(BlockRule.反吟元旦盤(MntDir.山), ChartMntRules.reversed(it.getChartBlockFromSymbol(Symbol.離)))
    }

    // 另一範例： 艮山坤向： 五運 山方山星 與 向方向星 犯反吟
    ChartMntContext.getChartMnt(5, Mountain.艮).also {
      assertEquals(BlockRule.反吟元旦盤(MntDir.山), ChartMntRules.reversed(it.getChartBlockFromSymbol(Symbol.艮)))
      assertEquals(BlockRule.反吟元旦盤(MntDir.向), ChartMntRules.reversed(it.getChartBlockFromSymbol(Symbol.坤)))
    }

    // 另一範例： 申山寅向： 五運 山方向星 與 向方山星 犯反吟
    ChartMntContext.getChartMnt(5, Mountain.申).also {
      assertEquals(BlockRule.反吟元旦盤(MntDir.山), ChartMntRules.reversed(it.getChartBlockFromSymbol(Symbol.坤)))
      assertEquals(BlockRule.反吟元旦盤(MntDir.向), ChartMntRules.reversed(it.getChartBlockFromSymbol(Symbol.艮)))
    }
  }
}