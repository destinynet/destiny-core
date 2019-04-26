/**
 * Created by smallufo on 2019-04-26.
 */
package destiny.iching

import kotlin.math.absoluteValue

/**
 * 先天六十四卦工具程式
 * (for 伏羲先天六十四卦天圓地方圖)
 */
object congenital {

  /**
   * 取得此卦於先天卦氣的座標
   * 傳回的是 pair , 先下卦、再上卦
   */
  private fun getCoordinate(hex: IHexagram): Pair<Int, Int> {
    return SymbolCongenital.getIndex(hex.lowerSymbol) to SymbolCongenital.getIndex(hex.upperSymbol)
  }

  fun next1(hex: IHexagram): Hexagram {
    return getCoordinate(hex).let { (low, up) ->
      (up + 1).let { if (it > 8) (1 to true) else (it to false) }.let { (upper, progressed) ->
        (low + (if (progressed) 1 else 0)).let { if (it > 8) 1 else it }.let { lower ->
          Hexagram.of(SymbolCongenital.getSymbol(upper), SymbolCongenital.getSymbol(lower))
        }
      }
    }
  }

  fun prev1(hex: IHexagram): Hexagram {
    return getCoordinate(hex).let { (low, up) ->
      (up - 1).let { if (it == 0) (8 to true) else (it to false) }.let { (upper, fallen) ->
        (low - (if (fallen) 1 else 0)).let { if (it == 0) 8 else it }.let { lower ->
          Hexagram.of(SymbolCongenital.getSymbol(upper), SymbolCongenital.getSymbol(lower))
        }
      }
    }
  }

  fun IHexagram.next(): Hexagram {
    return next1(this)
  }

  fun IHexagram.prev(): Hexagram {
    return prev1(this)
  }

  fun IHexagram.next(n: Int): Hexagram {
    return if (n == 0) {
      Hexagram.of(this)
    } else {
      if (n > 0) {
        when {
          n == 1 -> next1(this)
          n > 64 -> next(n - 64)
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
          n < -64 -> next(n + 64)
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
   * 以時序求出下一個卦 (or 上一個卦)
   */
  fun IHexagram.timeSeq(forward: Boolean = true) : Hexagram {
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
}