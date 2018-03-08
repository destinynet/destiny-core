/**
 * Created by smallufo on 2015-05-13.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import destiny.iching.Symbol.*

enum class Yuan {
  UP,  // 上元
  MID, // 中元
  LOW  // 下元
}

/** 九宮飛星 , 卦序推演 */
val symbolPeriods: List<Symbol?> = listOf(null, 乾, 兌, 艮, 離, 坎, 坤, 震, 巽)