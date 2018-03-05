/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.fengshui.sanyuan

import destiny.core.Position
import destiny.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ChartMntPresenterTest {

  /**
  14 　 68 　 86
  巽六　離二　坤四
  　　　　　　　　
  95 　 23 　 41
  震五　中七　兌九
  　　　　　　　　
  59 　 77 　 32
  艮一　坎三　乾八
   */
  @Test
  fun `七運，午山子向 , 坎底`() {
    val chart = ChartMntContext.getChartPresenter(7, Mountain.午, Symbol.坎)
    println(chart.posMap)
  }

  @Test
  fun `七運，午山子向 , 乾底`() {
    val chart = ChartMntContext.getChartPresenter(7, Mountain.午, Symbol.乾)
    println(chart.posMap)
  }

  /**
   * 七運,乾山巽向 , 參考結果  https://imgur.com/Z6TIe0X
   *
   * 過程推演 https://kknews.cc/zh-tw/geomantic/lppj4bg.html
   */
  @Test
  fun `七運,乾山巽向`() {
    val chart = ChartMntContext.getChartPresenter(7, Mountain.乾, Symbol.坎)
    assertEquals(ChartBlock(Symbol.坎, 4, 2, 3), chart.getChartBlockFromPosition(Position.B))
    assertEquals(ChartBlock(Symbol.艮, 2, 9, 1), chart.getChartBlockFromPosition(Position.LB))
    assertEquals(ChartBlock(Symbol.震, 6, 4, 5), chart.getChartBlockFromPosition(Position.L))
    assertEquals(ChartBlock(Symbol.巽, 7, 5, 6), chart.getChartBlockFromPosition(Position.LU))
    assertEquals(ChartBlock(Symbol.離, 3, 1, 2), chart.getChartBlockFromPosition(Position.U))
    assertEquals(ChartBlock(Symbol.坤, 5, 3, 4), chart.getChartBlockFromPosition(Position.RU))
    assertEquals(ChartBlock(Symbol.兌, 1, 8, 9), chart.getChartBlockFromPosition(Position.R))
    assertEquals(ChartBlock(Symbol.乾, 9, 7, 8), chart.getChartBlockFromPosition(Position.RB))
  }


  /**
   * 七運，午山子向
   *
   * 14  68  86
   * 六  二  四
   *
   * 95  23  41
   * 五  七  九
   *
   * 59  77  32
   * 一  三  八
   *
   * 以不同的「底邊」去看，得到的 ChartBlock 應該一致
   */
  @Test
  fun `不同的觀點（視角）不會影響結果`() {

    val chart坎底 = ChartMntContext.getChartPresenter(7, Mountain.午, Symbol.坎)
    println(chart坎底)
    assertEquals(ChartBlock(Symbol.坎, 7, 7, 3), chart坎底.getChartBlockFromPosition(Position.B))
    assertEquals(ChartBlock(Symbol.艮, 5, 9, 1), chart坎底.getChartBlockFromPosition(Position.LB))
    assertEquals(ChartBlock(Symbol.震, 9, 5, 5), chart坎底.getChartBlockFromPosition(Position.L))
    assertEquals(ChartBlock(Symbol.巽, 1, 4, 6), chart坎底.getChartBlockFromPosition(Position.LU))
    assertEquals(ChartBlock(Symbol.離, 6, 8, 2), chart坎底.getChartBlockFromPosition(Position.U))
    assertEquals(ChartBlock(Symbol.坤, 8, 6, 4), chart坎底.getChartBlockFromPosition(Position.RU))
    assertEquals(ChartBlock(Symbol.兌, 4, 1, 9), chart坎底.getChartBlockFromPosition(Position.R))
    assertEquals(ChartBlock(Symbol.乾, 3, 2, 8), chart坎底.getChartBlockFromPosition(Position.RB))

    val chart乾底 = ChartMntContext.getChartPresenter(7, Mountain.午, Symbol.乾)
    assertEquals(ChartBlock(Symbol.乾, 3, 2, 8), chart乾底.getChartBlockFromPosition(Position.B))
    assertEquals(ChartBlock(Symbol.坎, 7, 7, 3), chart乾底.getChartBlockFromPosition(Position.LB))
    assertEquals(ChartBlock(Symbol.艮, 5, 9, 1), chart乾底.getChartBlockFromPosition(Position.L))
    assertEquals(ChartBlock(Symbol.震, 9, 5, 5), chart乾底.getChartBlockFromPosition(Position.LU))
    assertEquals(ChartBlock(Symbol.巽, 1, 4, 6), chart乾底.getChartBlockFromPosition(Position.U))
    assertEquals(ChartBlock(Symbol.離, 6, 8, 2), chart乾底.getChartBlockFromPosition(Position.RU))
    assertEquals(ChartBlock(Symbol.坤, 8, 6, 4), chart乾底.getChartBlockFromPosition(Position.R))
    assertEquals(ChartBlock(Symbol.兌, 4, 1, 9), chart乾底.getChartBlockFromPosition(Position.RB))

  }

  /**
   * 當五黃如中時，因為五黃本身沒有山向，五黃是中宮戊己土，那麼，
   * 五入中的順飛還是逆飛則是由五所在的宮位的山向的陰陽，性質決定。
   * 如一運子山午向，運星五到離，五入中為向星後，按五所在的「午」的性質決定，故逆飛。
   *
   * 一運，子山 (五入中 , 為向 , 午向 , 午為陰 , 逆飛)
   *
   * 56  11  38
   * 九  五  七
   *
   * 47  65  83
   * 八  一  三
   *
   * 92  29  74
   * 四  六  二
   */
  @Test
  fun `一運子山午向，運星五到離，五入中為向星`() {
    val chart = ChartMntContext.getChartPresenter(1, Mountain.子, Symbol.坎)
    assertEquals(ChartBlock(Symbol.坎, 2, 9, 6), chart.getChartBlockFromPosition(Position.B))
    assertEquals(ChartBlock(Symbol.艮, 9, 2, 4), chart.getChartBlockFromPosition(Position.LB))
    assertEquals(ChartBlock(Symbol.震, 4, 7, 8), chart.getChartBlockFromPosition(Position.L))
    assertEquals(ChartBlock(Symbol.巽, 5, 6, 9), chart.getChartBlockFromPosition(Position.LU))
    assertEquals(ChartBlock(Symbol.離, 1, 1, 5), chart.getChartBlockFromPosition(Position.U))
    assertEquals(ChartBlock(Symbol.坤, 3, 8, 7), chart.getChartBlockFromPosition(Position.RU))
    assertEquals(ChartBlock(Symbol.兌, 8, 3, 3), chart.getChartBlockFromPosition(Position.R))
    assertEquals(ChartBlock(Symbol.乾, 7, 4, 2), chart.getChartBlockFromPosition(Position.RB))
  }
}