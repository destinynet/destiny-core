/**
 * Created by smallufo on 2018-02-27.
 */

package destiny.FengShui.SanYuan

import destiny.tools.ChineseStringTools

fun Int.toChineseDigit(): String {
  return ChineseStringTools.digitToChinese(this)
}

fun Int.toBiggerDigit(): String {
  return when (this) {
    1 -> "１"
    2 -> "２"
    3 -> "３"
    4 -> "４"
    5 -> "５"
    6 -> "６"
    7 -> "７"
    8 -> "８"
    9 -> "９"
    else -> ChineseStringTools.NULL_CHAR
  }
}