/**
 * @author smallufo
 * Created on 2007/3/8 at 上午 3:44:36
 */
package destiny.iching

import destiny.core.chinese.IYinYang
import java.io.Serializable

/**
 * 一個最基本的「卦」的資料，只有 取得 各爻陰陽 getLine(int index) / 取得六爻陰陽 getLines()  / 上卦 getUpperSymbol() / 下卦 getLowerSymbol() / 等介面
 */
interface IHexagram : Serializable {

  /** 取得全部的 [IYinYang] 值 */
  val yinYangs: List<IYinYang>

  /** 取得上卦  */
  val upperSymbol: Symbol
    get() = yinYangs.subList(3, 6).let { Symbol.of(it) }

  /** 取得下卦  */
  val lowerSymbol: Symbol
    get() = yinYangs.subList(0, 3).let { Symbol.of(it) }

  val unicode: Char
    get() {
      return unicodeMap.getValue(Pair(upperSymbol, lowerSymbol))
    }

  /** 取得全部的 boolean 值 */
  val booleans: List<Boolean>
    get() = yinYangs.map { it.booleanValue }

  /** 取得 010101 的表示法  */
  val binaryCode: String
    get() = booleans.joinToString(separator = "", transform = { b -> if (b) "1" else "0" })


  /** 取得第幾爻的陰陽 , 為了方便起見，index 為 1 至 6  */
  fun getBoolean(lineIndex: Int): Boolean {
    require(lineIndex in 1..6) { "lineIndex out of range , 1 <= lineIndex <= 6 : $lineIndex" }
    when (lineIndex) {
      1 -> return lowerSymbol.getBooleanValue(1)
      2 -> return lowerSymbol.getBooleanValue(2)
      3 -> return lowerSymbol.getBooleanValue(3)
      4 -> return upperSymbol.getBooleanValue(1)
      5 -> return upperSymbol.getBooleanValue(2)
      6 -> return upperSymbol.getBooleanValue(3)
    }
    throw RuntimeException("index out of range , 1 <= index <= 6 : $lineIndex")
  }

  /** 取得第幾爻的陰陽 , 為了方便起見，index 為 1 至 6  */
  fun getYinYang(lineIndex: Int): IYinYang {
    require(lineIndex in 1..6) { "lineIndex out of range , 1 <= lineIndex <= 6 : $lineIndex" }
    return yinYangs[lineIndex - 1]
  }


  /**
   * 第 line 爻動的話，變卦的陰陽是什麼
   * @param lines [1~6]
   */
  fun getTargetYinYangs(vararg lines: Int): List<Boolean> {
    return yinYangs.mapIndexed { index, yy -> if (lines.contains(index + 1)) !yy.booleanValue else yy.booleanValue }
  }

  /**
   * 取得動爻列表 , 傳回的 Set<Int> 其範圍在 1~6 之間 (即 lineIndex)
   */
  fun getMotivatedLines(dst: IHexagram): Set<Int> {
    return if (booleans == dst.booleans) {
      emptySet()
    } else {
      booleans.mapIndexed { index, b -> Pair(index + 1, b) }
        .filter { (lineIndex, b) -> dst.getBoolean(lineIndex) != b }
        .map { (lineIndex, _) -> lineIndex }
        .toSet()
    }
  }

  /**
   * 互卦 , 去掉初爻、上爻，中間四爻延展出去，故用 Middle Span Hexagram 為名
   */
  val middleSpanHexagram: IHexagram
    get() = Hexagram.ofYinYangs(
      listOf(
        yinYangs[1], yinYangs[2], yinYangs[3],
        yinYangs[2], yinYangs[3], yinYangs[4])
    )

  /**
   * 錯卦 , 一卦六爻全變 , 交錯之意 , 故取名 Interlaced Hexagram
   */
  val interlacedHexagram: IHexagram
    get() = Hexagram.of(booleanArrayOf(
      !yinYangs[0].booleanValue,
      !yinYangs[1].booleanValue,
      !yinYangs[2].booleanValue,
      !yinYangs[3].booleanValue,
      !yinYangs[4].booleanValue,
      !yinYangs[5].booleanValue)
    )

  /**
   * 綜卦 , 上下顛倒 , 故取名 Reversed Hexagram
   */
  val reversedHexagram: IHexagram
    get() = Hexagram.ofYinYangs(yinYangs.reversed())

  /** 每卦各取 先天對沖 */
  val congenitalOpposition: IHexagram
    get() = Hexagram.of(
      SymbolCongenital.getOppositeSymbol(upperSymbol),
      SymbolCongenital.getOppositeSymbol(lowerSymbol)
    )

  /** 每卦各取 後天對沖 */
  val acquiredOpposition: IHexagram
    get() = Hexagram.of(
      SymbolAcquired.getOppositeSymbol(upperSymbol),
      SymbolAcquired.getOppositeSymbol(lowerSymbol)
    )

  companion object {
    private val unicodeMap: Map<Pair<Symbol, Symbol>, Char> = mapOf(
      (Pair(Symbol.乾, Symbol.乾) to '䷀'),
      (Pair(Symbol.坤, Symbol.坤) to '䷁'),
      (Pair(Symbol.坎, Symbol.震) to '䷂'),
      (Pair(Symbol.艮, Symbol.坎) to '䷃'),
      (Pair(Symbol.坎, Symbol.乾) to '䷄'),
      (Pair(Symbol.乾, Symbol.坎) to '䷅'),
      (Pair(Symbol.坤, Symbol.坎) to '䷆'),
      (Pair(Symbol.坎, Symbol.坤) to '䷇'),
      (Pair(Symbol.巽, Symbol.乾) to '䷈'),
      (Pair(Symbol.乾, Symbol.兌) to '䷉'),
      (Pair(Symbol.坤, Symbol.乾) to '䷊'),
      (Pair(Symbol.乾, Symbol.坤) to '䷋'),
      (Pair(Symbol.乾, Symbol.離) to '䷌'),
      (Pair(Symbol.離, Symbol.乾) to '䷍'),
      (Pair(Symbol.坤, Symbol.艮) to '䷎'),
      (Pair(Symbol.震, Symbol.坤) to '䷏'),
      (Pair(Symbol.兌, Symbol.震) to '䷐'),
      (Pair(Symbol.艮, Symbol.巽) to '䷑'),
      (Pair(Symbol.坤, Symbol.兌) to '䷒'),
      (Pair(Symbol.巽, Symbol.坤) to '䷓'),
      (Pair(Symbol.離, Symbol.震) to '䷔'),
      (Pair(Symbol.艮, Symbol.離) to '䷕'),
      (Pair(Symbol.艮, Symbol.坤) to '䷖'),
      (Pair(Symbol.坤, Symbol.震) to '䷗'),
      (Pair(Symbol.乾, Symbol.震) to '䷘'),
      (Pair(Symbol.艮, Symbol.乾) to '䷙'),
      (Pair(Symbol.艮, Symbol.震) to '䷚'),
      (Pair(Symbol.兌, Symbol.巽) to '䷛'),
      (Pair(Symbol.坎, Symbol.坎) to '䷜'),
      (Pair(Symbol.離, Symbol.離) to '䷝'),
      (Pair(Symbol.兌, Symbol.艮) to '䷞'),
      (Pair(Symbol.震, Symbol.巽) to '䷟'),
      (Pair(Symbol.乾, Symbol.艮) to '䷠'),
      (Pair(Symbol.震, Symbol.乾) to '䷡'),
      (Pair(Symbol.離, Symbol.坤) to '䷢'),
      (Pair(Symbol.坤, Symbol.離) to '䷣'),
      (Pair(Symbol.巽, Symbol.離) to '䷤'),
      (Pair(Symbol.離, Symbol.兌) to '䷥'),
      (Pair(Symbol.坎, Symbol.艮) to '䷦'),
      (Pair(Symbol.震, Symbol.坎) to '䷧'),
      (Pair(Symbol.艮, Symbol.兌) to '䷨'),
      (Pair(Symbol.巽, Symbol.震) to '䷩'),
      (Pair(Symbol.兌, Symbol.乾) to '䷪'),
      (Pair(Symbol.乾, Symbol.巽) to '䷫'),
      (Pair(Symbol.兌, Symbol.坤) to '䷬'),
      (Pair(Symbol.坤, Symbol.巽) to '䷭'),
      (Pair(Symbol.兌, Symbol.坎) to '䷮'),
      (Pair(Symbol.坎, Symbol.巽) to '䷯'),
      (Pair(Symbol.兌, Symbol.離) to '䷰'),
      (Pair(Symbol.離, Symbol.巽) to '䷱'),
      (Pair(Symbol.震, Symbol.震) to '䷲'),
      (Pair(Symbol.艮, Symbol.艮) to '䷳'),
      (Pair(Symbol.巽, Symbol.艮) to '䷴'),
      (Pair(Symbol.震, Symbol.兌) to '䷵'),
      (Pair(Symbol.震, Symbol.離) to '䷶'),
      (Pair(Symbol.離, Symbol.艮) to '䷷'),
      (Pair(Symbol.巽, Symbol.巽) to '䷸'),
      (Pair(Symbol.兌, Symbol.兌) to '䷹'),
      (Pair(Symbol.巽, Symbol.坎) to '䷺'),
      (Pair(Symbol.坎, Symbol.兌) to '䷻'),
      (Pair(Symbol.巽, Symbol.兌) to '䷼'),
      (Pair(Symbol.震, Symbol.艮) to '䷽'),
      (Pair(Symbol.坎, Symbol.離) to '䷾'),
      (Pair(Symbol.離, Symbol.坎) to '䷿')
    )
  }
}
