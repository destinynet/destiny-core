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


  companion object {

    fun of(upper: Symbol, lower: Symbol): Hexagram {
      return values().first {
        it.upperSymbol === upper && it.lowerSymbol === lower
      }
    }


    /**
     * 由卦序傳回卦
     * @param index 1 <= 卦序 <= 64
     * @param sequence 實作 getIndex(Hexagram) 的介面
     */
    fun of(index: Int, sequence: IHexagramSequence = HexagramDefaultComparator()): IHexagram {
      if (index > 64)
        return of(index % 64, sequence)
      return if (index <= 0) of(index + 64, sequence) else sequence.getHexagram(index)

    }

    /** 從 陰陽 YinYang 實體的 array 傳回 Hexagram  */
    fun of(yinYangs: Array<IYinYang>): Hexagram {
      require(yinYangs.size == 6) { "yinYangs size != 6" }

      val upper = Symbol.getSymbol(yinYangs[3].booleanValue, yinYangs[4].booleanValue, yinYangs[5].booleanValue)
      val lower = Symbol.getSymbol(yinYangs[0].booleanValue, yinYangs[1].booleanValue, yinYangs[2].booleanValue)
      return of(upper, lower)
    }

//    fun of(yinYangs : List<IYinYang>) : Hexagram {
//      require(yinYangs.size == 6) { "yinYangs size != 6" }
//      val upper = Symbol.getSymbol(yinYangs[3].booleanValue, yinYangs[4].booleanValue, yinYangs[5].booleanValue)
//      val lower = Symbol.getSymbol(yinYangs[0].booleanValue, yinYangs[1].booleanValue, yinYangs[2].booleanValue)
//      return of(upper, lower)
//    }

    /**
     * 由六爻的 boolean array 尋找卦象
     *
     * @param booleans [0~5]
     * 六爻陰陽，由初爻至上爻
     * @return 卦的實體(Hexagram)
     */
    fun of(booleans: BooleanArray): Hexagram {
      require(booleans.size == 6) { "booleans size != 6" }
      val lower = Symbol.getSymbol(booleans[0], booleans[1], booleans[2])
      val upper = Symbol.getSymbol(booleans[3], booleans[4], booleans[5])

      return Hexagram.of(upper, lower)
    }

    fun of(booleans: List<Boolean>): Hexagram {
      require(booleans.size == 6) { "booleans length is not 6 . content : $booleans" }

      val lower = Symbol.getSymbol(booleans[0], booleans[1], booleans[2])
      val upper = Symbol.getSymbol(booleans[3], booleans[4], booleans[5])
      return Hexagram.of(upper, lower)
    }


    fun of(hex: IHexagram): Hexagram {
      return of(hex.yinYangs)
    }

    /**
     * @return 從 六爻 (6,7,8 or 9) 取得本卦以及變卦
     */
    fun getHexagrams(lines: List<Int>): Pair<IHexagram, IHexagram> {
      val src = lines.map { i -> i % 2 == 1 }.toList()
      val dst = lines.map { i -> i == 6 || i == 7 }.toList()
      return Pair<IHexagram, IHexagram>(of(src), of(dst))
    }

    /** 從 "010101" 取得一個卦 */
    fun getFromBinaryString(code: String) : IHexagram {
      val array = BooleanArray(6)
      return try {
        for (i in 0..5) {
          val c = code.toCharArray()[i]
          array[i] = c != '0'
        }
        Hexagram.of(array)
      } catch (e: Exception) {
        Hexagram.乾
      }

    }
  }
}
