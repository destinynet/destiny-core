/**
 * Created by smallufo on 2018-02-15.
 */
package destiny.iching

import destiny.core.chinese.IYinYang
import destiny.core.chinese.YinYang
import destiny.iching.contentProviders.*
import java.io.Serializable
import java.util.*


data class LineText(
  /** 陰陽 */
  val yinYang: IYinYang,
  /** 爻辭 */
  val expression: String,
  /** 象曰 */
  val image: String) : IYinYang by yinYang, Serializable

interface IHexagramName {
  /** 短卦名 , 一個中文字 */
  val shortName: String
  /** 完整卦名 , 三或四個中文字 */
  val fullName: String
}

data class HexagramName(
  /** 短卦名 , 一個中文字 */
  override val shortName: String,
  /** 完整卦名 , 三或四個中文字 */
  override val fullName: String) : IHexagramName, Serializable


/** 完整一個卦象的所有文字 */
interface IHexagramText : IHexagramName, IHexagram {

  /** 卦名 */
  val hexagramName: IHexagramName
  /** 卦辭 */
  val expression: String
  /** 象曰 */
  val image: String
  /** 彖曰 */
  val judgement: String
  /** 六爻 的爻辭、象曰 */
  val lineTexts: List<LineText>
  /** 用九、用六 的爻辭、象曰 */
  val extraLine: LineText?

  /** 取得第幾爻 , 1<= index <= 6 */
  fun getLineFromOne(index: Int): LineText {
    return lineTexts[index - 1]
  }


}

data class HexagramText(
  override val hexagramName: HexagramName,
  /** 卦辭 */
  override val expression: String,
  /** 象曰 */
  override val image: String,
  /** 彖曰 */
  override val judgement: String,
  /** 六爻 */
  override val lineTexts: List<LineText>,
  /** 用九、用六 的爻辭、象曰 */
  override val extraLine: LineText?) : IHexagramText, IHexagramName by hexagramName, Serializable {

  override val yinYangs: List<IYinYang> = lineTexts
}

class HexagramTextContext(private val hexagramNameFull: IHexagramNameFull,
                          private val hexagramNameShort: IHexagramNameShort,
                          private val hexExpressionImpl: IHexProvider<String, String>,
                          private val imageImpl: IImage,
                          private val hexJudgement: IHexJudgement) : IHexagramProvider<IHexagramText>, Serializable {

  override fun getHexagram(hex: IHexagram, locale: Locale): IHexagramText {

    val shortName = hexagramNameShort.getNameShort(hex, locale)
    val fullName = hexagramNameFull.getNameFull(hex, locale)
    val hexExpression = hexExpressionImpl.getHexagram(hex, locale)
    val hexImage = imageImpl.getHexagramImage(hex, locale)
    val judgement = hexJudgement.getHexagram(hex, locale)

    val lineTexts: List<LineText> = (1..6).map { lineIndex ->
      val lineExpression = hexExpressionImpl.getLine(hex, lineIndex, locale)
      val lineImage = imageImpl.getLineImage(hex, lineIndex, locale)
      LineText(hex.getYinYang(lineIndex), lineExpression, lineImage)
    }.toList()

    val seq: IHexagramSequence = HexagramDefaultComparator()
    val extraLine: LineText? = seq.getIndex(hex).let {
      if (it == 1 || it == 2) {
        val lineExpression = hexExpressionImpl.getExtraLine(hex, locale)
        val lineImage = imageImpl.getExtraImage(hex, locale)
        // 用九 or 用六
        val use9or6 = if (it == 1) YinYang.陽 else YinYang.陰
        LineText(use9or6, lineExpression!!, lineImage!!)
      } else
        null
    }
    val hName = HexagramName(shortName, fullName)
    return HexagramText(hName, hexExpression, hexImage, judgement, lineTexts, extraLine)
  }
}