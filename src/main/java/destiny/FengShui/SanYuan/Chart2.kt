/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol

enum class Position {
  B,   // 底
  LB,  // 左下
  L, // 左
  LU,  // 左上
  U, // 上
  RU, // 右上
  R, // 右
  RB, // 右下
  C // 中間
}


data class Chart2(val period: Int,
                  val mountain: Mountain,
                  val view: Symbol,
                  val map: Map<Position, ChartBlock>)