/**
 * Created by smallufo on 2019-04-26.
 */
package destiny.core.iching

import kotlin.math.absoluteValue

/**
 * 先天六十四卦工具程式
 * (for 伏羲先天六十四卦天圓地方圖)
 */
object congenital {
  /**
   * 坤 = 0
   * 剝 = 1
   * ...
   * 大過 = 30
   * 姤 = 31
   *
   * ...
   *
   * 復 = 32
   * 頤 = 33
   * ...
   * 夬 = 62
   * 乾 = 63
   */
  fun Int.toHexagram(): Hexagram {
    return when {
      this < 0 -> (this + 64).toHexagram()
      this >= 64 -> (this - 64).toHexagram()
      else -> this.toString(radix = 2).padStart(6, '0')
        .let { Hexagram.getFromBinaryString(it) }
    }
  }

  fun IHexagram.toInt(): Int {
    return Integer.parseInt(this.binaryCode, 2)
  }


  /**
   * 方圖
   */
  object Table {
    private fun next1(hex: IHexagram): Hexagram {
      return (hex.toInt() - 1).let {
        if (it == -1)
          63
        else
          it
      }.toHexagram()
    }

    private fun prev1(hex: IHexagram): Hexagram {
      return (hex.toInt() + 1).let {
        if (it == 64)
          0
        else
          it
      }.toHexagram()
    }

    /**
     * 「方圖」角度的 next()
     */
    fun IHexagram.tableNext(): Hexagram {
      return next1(this)
    }

    /**
     * 「方圖」角度的 prev()
     */
    fun IHexagram.tablePrev(): Hexagram {
      return prev1(this)
    }

    /**
     * 「方圖」角度的 next
     * n 可為正值 , 也可為負值 , 也可為 0
     */
    fun IHexagram.tableNext(n: Int): Hexagram {

      // 傳回的是 pair , 先下卦、再上卦
      fun getCoordinate(hex: IHexagram): Pair<Int, Int> {
        return SymbolCongenital.getIndex(hex.lowerSymbol) to SymbolCongenital.getIndex(hex.upperSymbol)
      }

      return if (n == 0) {
        Hexagram.of(this)
      } else {
        if (n > 0) {
          when {
            n == 1 -> next1(this)
            n > 64 -> tableNext(n - 64)
            else -> getCoordinate(this).let { (low, up) ->
              (up + n).let {
                if (it % 8 == 0)
                  8 to (it / 8 - 1)
                else
                  it % 8 to it / 8
              }.let { (upper, addingRows) ->
                (low + addingRows).let {
                  if (it > 8)
                    it - 8
                  else
                    it
                }.let { lower ->
                  Hexagram.of(SymbolCongenital.getSymbol(upper), SymbolCongenital.getSymbol(lower))
                }
              }
            }
          }
        } else {
          when {
            n == -1 -> prev1(this)
            n < -64 -> tableNext(n + 64)
            else -> getCoordinate(this).let { (low, up) ->
              (up + n).let {
                when {
                  it % 8 == 0 -> 8 to (it.absoluteValue / 8 + 1)
                  it > 0 -> it to 0
                  else -> it % 8 to (it.absoluteValue / 8) + 1
                }
              }.let { (upper, minusRows) ->
                (low - minusRows).let {
                  if (it <= 0)
                    it + 8
                  else
                    it
                }.let { lower ->
                  Hexagram.of(SymbolCongenital.getSymbol(upper), SymbolCongenital.getSymbol(lower))
                }
              }
            }
          }
        }
      }
    }
  } // table


  /**
   * 「圓圖」
   * 時序、圓圖 的角度、觀點
   */
  object Circle {
    /**
     * 此卦在「時序上」領先另一個卦 幾步
     * 例如
     * 冬至點為 復
     * 下個卦為 頤
     * 則 頤.aheadOf(復) = 1
     *
     * 冬至前為 坤
     * 坤.aheadOf(復) = 63
     */
    fun IHexagram.aheadOf(hex: IHexagram): Int {
      val front = this.toInt()
      val back = hex.toInt()
      return if (front >= 32 && back >= 32) {
        (front - back).let { if (it < 0) it + 64 else it }
      } else if (front < 32 && back < 32) {
        (back - front).let { if (it < 0) it + 64 else it }
      } else if (front < 32 && back >= 32) {
        (32 - front) + 63 - back
      } else {
        front - 31 + back
      }
    }

    /**
     * 承上 , 計算「落後」幾步
     */
    fun IHexagram.behindOf(hex: IHexagram): Int {
      return hex.aheadOf(this)
    }

    /**
     * 以時序求出下一個卦 (or 上一個卦)
     */
    fun IHexagram.next(forward: Boolean = true): Hexagram {
      return this.toInt().let {
        if (it >= 32) {
          // 乾、兌、離、震
          if (forward) {
            if (this == Hexagram.乾)
              Hexagram.姤
            else {
              (it + 1).toHexagram()
            }
          } else {
            if (this == Hexagram.復) {
              Hexagram.坤
            } else {
              (it - 1).toHexagram()
            }
          }
        } else {
          // 1~31 : 巽、坎、艮、坤
          if (forward) {
            if (this == Hexagram.坤)
              Hexagram.復
            else
              (it - 1).toHexagram()
          } else {
            if (this == Hexagram.姤)
              Hexagram.乾
            else {
              (it + 1).toHexagram()
            }
          }
        }
      }
    }

    fun IHexagram.next(): Hexagram {
      return this.next(true)
    }

    fun IHexagram.next(steps: Int): Hexagram {
      return generateSequence(this to 0) {
        it.first.next() to it.second+1
      }.first { it.second == steps }
        .first.let { Hexagram.of(it) }
    }

    fun IHexagram.prev(): Hexagram {
      return this.next(false)
    }

    fun IHexagram.prev(steps: Int) : Hexagram {
      return generateSequence(this to 0) {
        it.first.prev() to it.second+1
      }.first { it.second == steps }
        .first.let { Hexagram.of(it) }
    }
  } // circle
}

