/**
 * @author smallufo
 * Created on 2010/6/23 at 下午5:17:33
 */
package destiny.iching

import destiny.core.chinese.IYinYang
import java.io.Serializable

enum class Hexagram constructor(
  override val upperSymbol: Symbol,
  override val lowerSymbol: Symbol) : IHexagram, Serializable {
  乾(Symbol.乾, Symbol.乾),
  坤(Symbol.坤, Symbol.坤),
  屯(Symbol.坎, Symbol.震),
  蒙(Symbol.艮, Symbol.坎),
  需(Symbol.坎, Symbol.乾),
  訟(Symbol.乾, Symbol.坎),
  師(Symbol.坤, Symbol.坎),

  比(Symbol.坎, Symbol.坤),
  小畜(Symbol.巽, Symbol.乾),
  履(Symbol.乾, Symbol.兌),
  泰(Symbol.坤, Symbol.乾),
  否(Symbol.乾, Symbol.坤),

  同人(Symbol.乾, Symbol.離),
  大有(Symbol.離, Symbol.乾),
  謙(Symbol.坤, Symbol.艮),
  豫(Symbol.震, Symbol.坤),
  隨(Symbol.兌, Symbol.震),

  蠱(Symbol.艮, Symbol.巽),
  臨(Symbol.坤, Symbol.兌),
  觀(Symbol.巽, Symbol.坤),
  噬嗑(Symbol.離, Symbol.震),
  賁(Symbol.艮, Symbol.離),

  剝(Symbol.艮, Symbol.坤),
  復(Symbol.坤, Symbol.震),
  無妄(Symbol.乾, Symbol.震),
  大畜(Symbol.艮, Symbol.乾),
  頤(Symbol.艮, Symbol.震),

  大過(Symbol.兌, Symbol.巽),
  坎(Symbol.坎, Symbol.坎),
  離(Symbol.離, Symbol.離),

  咸(Symbol.兌, Symbol.艮),
  恆(Symbol.震, Symbol.巽),
  遯(Symbol.乾, Symbol.艮),
  大壯(Symbol.震, Symbol.乾),

  晉(Symbol.離, Symbol.坤),
  明夷(Symbol.坤, Symbol.離),
  家人(Symbol.巽, Symbol.離),
  睽(Symbol.離, Symbol.兌),

  蹇(Symbol.坎, Symbol.艮),
  解(Symbol.震, Symbol.坎),
  損(Symbol.艮, Symbol.兌),
  益(Symbol.巽, Symbol.震),
  夬(Symbol.兌, Symbol.乾),
  姤(Symbol.乾, Symbol.巽),
  萃(Symbol.兌, Symbol.坤),

  升(Symbol.坤, Symbol.巽),
  困(Symbol.兌, Symbol.坎),
  井(Symbol.坎, Symbol.巽),
  革(Symbol.兌, Symbol.離),
  鼎(Symbol.離, Symbol.巽),
  震(Symbol.震, Symbol.震),

  艮(Symbol.艮, Symbol.艮),
  漸(Symbol.巽, Symbol.艮),
  歸妹(Symbol.震, Symbol.兌),
  豐(Symbol.震, Symbol.離),
  旅(Symbol.離, Symbol.艮),
  巽(Symbol.巽, Symbol.巽),

  兌(Symbol.兌, Symbol.兌),
  渙(Symbol.巽, Symbol.坎),
  節(Symbol.坎, Symbol.兌),
  中孚(Symbol.巽, Symbol.兌),

  小過(Symbol.震, Symbol.艮),
  既濟(Symbol.坎, Symbol.離),
  未濟(Symbol.離, Symbol.坎);




  /**
   * 第 line 爻動的話，變卦是什麼
   * @param lines [1~6]
   */
  override fun getHexagram(vararg lines: Int): IHexagram {
    return yinYangs
      .mapIndexed { index, b -> if (lines.contains(index + 1)) !b else b }
      .let { Hexagram.getHexagram(it) }
  }

  companion object {

    fun getHexagram(upper: Symbol, lower: Symbol): Hexagram {
      return values().first {
        it.upperSymbol === upper && it.lowerSymbol === lower
      }
    }


    /**
     * 由卦序傳回卦
     * @param index 1 <= 卦序 <= 64
     * @param sequence 實作 getIndex(Hexagram) 的介面
     */
    fun getHexagram(index: Int, sequence: IHexagramSequence): IHexagram {
      if (index > 64)
        return getHexagram(index % 64, sequence)
      return if (index <= 0) getHexagram(index + 64, sequence) else sequence.getHexagram(index)

    }

    /** 從 陰陽 YinYang 實體的 array 傳回 HexagramIF  */
    fun getHexagram(yinyangs: Array<IYinYang>): Hexagram {
      if (yinyangs.size != 6)
        throw RuntimeException("yinyangs length not equal 6 !")

      val upper = Symbol.getSymbol(yinyangs[3].booleanValue, yinyangs[4].booleanValue, yinyangs[5].booleanValue)
      val lower = Symbol.getSymbol(yinyangs[0].booleanValue, yinyangs[1].booleanValue, yinyangs[2].booleanValue)
      return getHexagram(upper, lower)
    }

    /**
     * 由六爻的 boolean array 尋找卦象
     *
     * @param booleans [0~5]
     * 六爻陰陽，由初爻至上爻
     * @return 卦的實體(Hexagram)
     */
    fun getHexagram(booleans: BooleanArray): Hexagram {
      if (booleans.size != 6)
        throw RuntimeException("booleans length is not 6 , the length is " + booleans.size)
      val lower = Symbol.getSymbol(booleans[0], booleans[1], booleans[2])
      val upper = Symbol.getSymbol(booleans[3], booleans[4], booleans[5])

      return Hexagram.getHexagram(upper, lower)
    }

    fun getHexagram(booleans: List<Boolean>): Hexagram {
      require(booleans.size == 6) { "booleans length is not 6 . content : $booleans" }

      val lower = Symbol.getSymbol(booleans[0], booleans[1], booleans[2])
      val upper = Symbol.getSymbol(booleans[3], booleans[4], booleans[5])
      return Hexagram.getHexagram(upper, lower)
    }


    fun getHexagram(iHexagram: IHexagram): Hexagram {
      return getHexagram(iHexagram.yinYangs)
    }

    /**
     * @return 從 六爻 (6,7,8 or 9) 取得本卦以及變卦
     */
    fun getHexagrams(lines: List<Int>): Pair<IHexagram, IHexagram> {
      val src = lines.map { i -> i % 2 == 1 }.toList()
      val dst = lines.map { i -> i == 6 || i == 7 }.toList()
      return Pair<IHexagram, IHexagram>(getHexagram(src), getHexagram(dst))
    }
  }
}
