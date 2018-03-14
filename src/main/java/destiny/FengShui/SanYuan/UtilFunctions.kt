/**
 * Created by smallufo on 2018-02-27.
 */

package destiny.fengshui.sanyuan

import destiny.tools.ChineseStringTools

fun Int.toChineseDigit(): String {
  return ChineseStringTools.digitToChinese(this)
}

fun Int.toBiggerDigit(): String {
  return ChineseStringTools.toBiggerDigit(this)
}