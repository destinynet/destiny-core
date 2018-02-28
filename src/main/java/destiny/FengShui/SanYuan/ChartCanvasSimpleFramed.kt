/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 帶有外框 的簡易 Chart Canvas
 * 高度 10 x 寬度 20
┌──┬──┬──┐
│７１│２６│９８│
│ 七 │ 三 │ 五 │
├──┼──┼──┤
│８９│６２│４４│
│ 六 │ 八 │ 一 │
├──┼──┼──┤
│３５│１７│５３│
│ 二 │ 四 │ 九 │
├────────┤
│xx運│yy山│zz向│
└────────┘
原文網址：https://read01.com/RMPAEE.html
 */
class ChartCanvasSimpleFramed(chart: IChartMntPresenter,
                              fore: String? = null,
                              bg: String? = null) : ColorCanvas(12, 20, ChineseStringTools.NULL_CHAR, fore, bg) {

  init {
    setText("┌──┬──┬──┐", 1, 1)
    setText("│７１│２６│９８│", 2, 1)
    setText("│ 七 │ 三 │ 五 │", 3, 1)
    setText("├──┼──┼──┤", 4, 1)
    setText("│８９│６２│４４│", 5, 1)
    setText("│ 六 │ 八 │ 一 │", 6, 1)
    setText("├──┼──┼──┤", 7, 1)
    setText("│３５│１７│５３│", 8, 1)
    setText("│ 二 │ 四 │ 九 │", 9, 1)
    setText("├──┴──┴──┤", 10, 1)
    setText("│03運　09山　15向│", 11, 1)
    setText("└────────┘", 12, 1)


    val coordinateMap = mapOf(
      Position.B  to Pair(8, 9),
      Position.LB to Pair(8, 3),
      Position.L  to Pair(5, 3),
      Position.LU to Pair(2, 3),
      Position.U  to Pair(2, 9),
      Position.RU to Pair(2, 15),
      Position.R  to Pair(5, 15),
      Position.RB to Pair(8, 15),
      Position.C  to Pair(5, 9))


    coordinateMap.forEach { (pos, pair) ->
      val chartBlock = chart.posMap[pos]!!
      val blockCanvas = ChartBlockCanvasSimple(chartBlock, fore, bg)
      add(blockCanvas, pair.first, pair.second)
    }

    setText(chart.period.toChineseDigit(), 11, 3)
    setText(chart.mnt.toString(), 11, 9)
    setText(chart.mnt.opposite.toString(), 11, 15)
  }
}
