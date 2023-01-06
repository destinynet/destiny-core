/**
 * Created by smallufo on 2019-03-20.
 */
package destiny.core.iching

import java.io.Serializable


/** 單一卦象 的資料結構 */
interface IHexData<HexT, LineT> : Serializable {
  val hexagram: Hexagram

  val hexValue: HexT

  fun getLine(lineIndex: Int): LineT

  val extraLine: LineT?
}


/** 卦辭、爻辭 */
interface IHexExpression : IHexData<String , String>
data class HexExpression(
  override val hexagram: Hexagram,
  override val hexValue: String,
  private val lines: List<String>,
  override val extraLine: String?) : IHexExpression {

  override fun getLine(lineIndex: Int): String {
    return lines[lineIndex - 1]
  }
}

/** 卦 或 爻 的象曰  */
interface IHexImage : IHexData<String , String>
data class HexImage(
  override val hexagram: Hexagram,
  override val hexValue: String,
  private val lines: List<String>,
  override val extraLine: String?) : IHexImage {
  override fun getLine(lineIndex: Int): String {
    return lines[lineIndex - 1]
  }
}

