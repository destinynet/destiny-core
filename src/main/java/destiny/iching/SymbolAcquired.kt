/**
 * 後天八卦
 * @author smallufo
 * Created on 2002/8/13 at 下午 05:24:40
 */
package destiny.iching

import destiny.iching.Symbol.*
import java.util.*

/**
 * <pre>
 * 後天八卦
 *
 * 巽4 | 離9 | 坤2
 * ----+-----+----
 * 震3 |     | 兌7
 * ----+-----+----
 * 艮8 | 坎1 | 乾6
 *
</pre> *
 */
class SymbolAcquired internal constructor() : Comparator<Symbol> {

  override fun compare(s1: Symbol, s2: Symbol): Int {
    return 後天八卦List.indexOf(s1) - 後天八卦List.indexOf(s2)
  }

  companion object {
    private val 後天八卦 = arrayOf(坎, 坤, 震, 巽, 乾, 兌, 艮, 離)

    private val 後天八卦List = listOf(*後天八卦)

    /**
     * 取得後天八卦的卦序
     * 坎1 , 坤2 , ... , 離 9
     * 因為沒有 5 , 所以得把 5 給跳過
     *
     *
     * 巽4 | 離9 | 坤2
     * ----+-----+----
     * 震3 |     | 兌7
     * ----+-----+----
     * 艮8 | 坎1 | 乾6
     *
     */
    fun getIndex(s: Symbol): Int {
      var tempIndex = 後天八卦List.indexOf(s) + 1
      if (tempIndex >= 5)
        tempIndex++
      return tempIndex
    }

    /**
     * 由後天八卦卦序取得 Symbol
     * 因為 '5' 為空，這裡不方便讓其支援 index = 0 以及 >=9 的情況
     *
     * 492
     * 357
     * 816
     *
     * index = 0 : Exception !
     * index = 1 , 後天卦 = 坎
     * index = 2 , 後天卦 = 坤
     * index = 3 , 後天卦 = 震
     * index = 4 , 後天卦 = 巽
     * index = 5 , 後天卦 = null
     * index = 6 , 後天卦 = 乾
     * index = 7 , 後天卦 = 兌
     * index = 8 , 後天卦 = 艮
     * index = 9 , 後天卦 = 離
     * index > 9 : Exception !
     */
    fun getSymbolNullable(index: Int): Symbol? {
      var i = index
      if (index == 5)
        return null
      else if (index > 5)
        i--
      return 後天八卦[i - 1]
    }

    /**
     * 2015-05-12 : 傳入後天卦的卦序，傳回八純卦
     * 巽4 | 離9 | 坤2
     * ----+-----+----
     * 震3 |  5  | 兌7
     * ----+-----+----
     * 艮8 | 坎1 | 乾6
     * 若傳入 5 , 則傳回 Optional.empty()
     * 若傳入 0 則傳回離卦
     * 傳入 10 , 則傳回坎卦 (10 % 9 = 1)
     */
    fun getSymbol(index: Int) : Symbol ? {
      val reminder = index % 9
      return when {
        reminder == 5 -> null
        reminder == 0 -> 離
        reminder > 5 -> 後天八卦[reminder-2]
        else -> 後天八卦[reminder-1]
      }
    }

    /**
     * 以順時針方向取得一卦
     */
    fun getClockwiseSymbol(s: Symbol): Symbol {
      return when (s) {
        坎 -> 艮
        坤 -> 兌
        震 -> 巽
        巽 -> 離
        乾 -> 坎
        兌 -> 乾
        艮 -> 震
        離 -> 坤
      }
    }

    /**
     * 逆時針
     */
    fun getCounterClockwiseSymbol(s: Symbol) : Symbol {
      return when(s) {
        巽 -> 震
        震 -> 艮
        艮 -> 坎
        坎 -> 乾
        乾 -> 兌
        兌 -> 坤
        坤 -> 離
        離 -> 巽
      }
    }

    /**
     * 取得對沖之卦
     */
    fun getOppositeSymbol(s: Symbol): Symbol {
      return when (s) {
        坎 -> 離
        坤 -> 艮
        震 -> 兌
        巽 -> 乾
        乾 -> 巽
        兌 -> 震
        艮 -> 坤
        離 -> 坎
      }
    }
  }
}
