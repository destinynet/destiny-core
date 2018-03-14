/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei

import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import destiny.iching.SymbolCongenital

/**
 * 紫微斗數配卦
 *
 * 取得此顆星，在 先天八卦 ([SymbolCongenital]) / 後天八卦 ([SymbolAcquired]) 的方位
 */
interface IStarSymbol {

  /** 後天八卦  */
  fun getSymbolAcquired(star: StarMain): Symbol

  /** 先天八卦 : 先求後天八卦，再 map 回先天八卦  */
  fun getSymbolCongenital(star: StarMain): Symbol {
    return getSymbolAcquired(star).toCongenital()
  }
}
