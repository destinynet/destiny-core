/**
 * Created by smallufo on 2019-03-20.
 */
package destiny.iching.contentProviders

import destiny.iching.Hexagram
import destiny.iching.IHexagram
import java.util.*

/** 針對「卦」所做的註解 */
interface IHex<HexT> {

  fun getHexagram(hex: IHexagram, locale: Locale = Locale.getDefault()): HexT
}

/** 針對「爻」所做的註解 */
interface IHexLine<LineT> {

  /**
   * @param lineIndex 1 <= lineIndex <= 6
   */
  fun getLine(hex: IHexagram, lineIndex: Int, locale: Locale = Locale.getDefault()): LineT

  fun getExtraLine(hex: IHexagram, locale: Locale = Locale.getDefault()): LineT?
}


/** 單一卦象 的資料結構 */
interface IHexData<HexT, LineT> {
  val hexagram : Hexagram

  val hexValue: HexT

  fun getLine(lineIndex: Int): LineT

  val extraLine: LineT?
}

interface IHexProvider<HexT, LineT> : IHex<HexT>, IHexLine<LineT> {

  fun getHexagramData(hex: IHexagram, locale: Locale = Locale.getDefault()): IHexData<HexT, LineT>
}


/** 短卦名 (中文為 一或兩字元) */
interface IHexNameShort : IHex<String>

/** 完整卦名 (中文為 三或四字元) */
interface IHexNameFull : IHex<String>

/** 卦辭、爻辭 */
data class HexExpression(
  override val hexagram: Hexagram,
  override val hexValue: String,
  private val lines: List<String>,
  override val extraLine: String?) : IHexData<String, String> {

  override fun getLine(lineIndex: Int): String {
    return lines[lineIndex - 1]
  }
}

/** 卦 或 爻 的象曰  */
data class HexImage(
  override val hexagram: Hexagram,
  override val hexValue: String,
  private val lines: List<String>,
  override val extraLine: String?) : IHexData<String, String> {
  override fun getLine(lineIndex: Int): String {
    return lines[lineIndex - 1]
  }
}

/** 彖曰 , 只有卦，才有彖曰 */
interface IHexJudgement : IHex<String>