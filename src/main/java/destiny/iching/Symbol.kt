/**
 * @author smallufo
 * Created on 2002/8/13 at 下午 01:08:17
 */
package destiny.iching

import destiny.core.chinese.FiveElement
import destiny.core.chinese.IFiveElement
import destiny.core.chinese.IYinYang
import java.io.Serializable


/**
 * 八卦基本符號以及其資料
 */
enum class Symbol(private val yinYangs: BooleanArray) : Serializable, ISymbol, IFiveElement {
  乾(booleanArrayOf(true, true, true)),
  兌(booleanArrayOf(true, true, false)),
  離(booleanArrayOf(true, false, true)),
  震(booleanArrayOf(true, false, false)),
  巽(booleanArrayOf(false, true, true)),
  坎(booleanArrayOf(false, true, false)),
  艮(booleanArrayOf(false, false, true)),
  坤(booleanArrayOf(false, false, false));

  override fun toString(): String {
    return name
  }

  /**
   * 取得八卦名
   */
  fun getName(): String {
    return name
  }

  /**
   * 實作 [ISymbol]
   * 取得一個卦的第幾爻
   * index 值為 1,2,或3
   */
  override fun getBooleanValue(index: Int): Boolean {
    return yinYangs[index - 1]
  }


  override val fiveElement: FiveElement
    get() = when (this) {
      乾, 兌 -> FiveElement.金
      離 -> FiveElement.火
      震, 巽 -> FiveElement.木
      坎 -> FiveElement.水
      艮, 坤 -> FiveElement.土
    }


  /** 先天八卦 -> 後天八卦  */
  fun toAcquired(): Symbol {
    return con2AcqMap[this]!!
  }

  /** 後天八卦 -> 先天八卦  */
  fun toCongenital(): Symbol {
    return con2AcqMap.map { (k, v) -> v to k }.toMap()[this]!!
  }

  companion object {

    /** 取得八個卦 */
    private val symbols = arrayOf(乾, 兌, 離, 震, 巽, 坎, 艮, 坤)

    /** 先天八卦 -> 後天八卦 bi-mapping  */
    private val con2AcqMap = mapOf(
      乾 to 離,
      兌 to 巽,
      離 to 震,
      震 to 艮,
      巽 to 坤,
      坎 to 兌,
      艮 to 乾,
      坤 to 坎)


    /**
     * 「由下而上」 三個陰陽 , 查詢卦象為何
     */
    fun getSymbol(line: Array<IYinYang>): Symbol {
      return getSymbol(line[0].booleanValue, line[1].booleanValue, line[2].booleanValue)
    }

    fun of(yinYangs: List<IYinYang>): Symbol {

      return symbols.first {
        it.yinYangs[0] == yinYangs[0].booleanValue &&
          it.yinYangs[1] == yinYangs[1].booleanValue &&
          it.yinYangs[2] == yinYangs[2].booleanValue
      }
    }

    /**
     * 「由下而上」 三個陰陽 , 查詢卦象為何
     */
    fun getSymbol(vararg lines: Boolean): Symbol {
      return symbols.first {
        lines[0] == it.yinYangs[0] &&
          lines[1] == it.yinYangs[1] &&
          lines[2] == it.yinYangs[2]
      }
    }

    /**
     * 由 三個陰陽 , 查詢卦象為何 , 比較標準的命名方式
     */
    fun valueOf(yinYangs: Array<IYinYang>): Symbol {
      return getSymbol(yinYangs)
    }
  }
}
