/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test

class ChartCanvasSimpleFramedTest {

  /**
   * 參考 : https://read01.com/zh-tw/RMPAEE.htm
  ┌──┬──┬──┐
  │７９│２５│９７│
  │巽七│離三│坤五│
  ├──┼──┼──┤
  │８８│６１│４３│
  │震六│中八│兌一│
  ├──┼──┼──┤
  │３４│１６│５２│
  │艮二│坎四│乾九│
  ├──┴──┴──┤
  │八運　甲山　庚向│
  └────────┘
   */
  @Test
  fun `八運 甲山庚向`() {
    val chart = ChartMntPresenter(8, Mountain.甲, Symbol.坎)
    val canvas = ChartCanvasSimpleFramed(chart)
    println(canvas.htmlOutput)

    println(canvas.toString())
  }

  @Test
  fun `八運 甲山庚向 , chart2`() {
//    val chart = ChartContext.getChart(8 , Mountain.甲 , Symbol.坎)
//    println(chart)
  }

  /** 印出 24山 */
  @Test
  fun print24Mountains() {
    Mountain.values().forEach { mnt ->
      val chart = ChartMntPresenter(7, mnt, Symbol.坎)
      val canvas = ChartCanvasSimpleFramed(chart)
      println(canvas.htmlOutput)
    }
  }
}