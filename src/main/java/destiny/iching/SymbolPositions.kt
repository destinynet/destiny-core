/**
 * Created by smallufo on 2018-03-05.
 */
package destiny.iching

import destiny.core.TriGrid.*
import destiny.iching.Symbol.*

object SymbolPositions {

  /** 對應到 [SymbolCongenital] */
  private val 先天 = mapOf(
    乾 to U,
    兌 to LU,
    離 to L,
    震 to LB,
    巽 to RU,
    坎 to R,
    艮 to RB,
    坤 to B)

  /** 對應到 [SymbolAcquired] */
  private val 後天 = mapOf(
    巽 to LU,
    震 to L,
    艮 to LB,
    離 to U,
    坎 to B,
    坤 to RU,
    兌 to R,
    乾 to RB
                )

  /**
   * 後天 -> 先天
   * 例如： 後天的乾位 , 位於 右下角 , 而先天卦的右下角 為艮 . ==> 傳回 艮
   * */
  fun acquiredToCongenital(symbol: Symbol): Symbol {
    return 後天[symbol].let { pos ->
      先天.entries.first { (_, p) -> p === pos }.key
    }
  }

  /**
   * 先天 -> 後天
   * 例如： 先天的乾位 , 位於上方 , 而後天卦的上方 為離 . ==> 傳回 離
   */
  fun congenitalToAcquired(symbol: Symbol): Symbol {
    return 先天[symbol].let { pos ->
      後天.entries.first { (_, p) -> p === pos }.key
    }
  }
}