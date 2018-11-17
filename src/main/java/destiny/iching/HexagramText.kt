/**
 * Created by smallufo on 2018-02-15.
 */
package destiny.iching

import destiny.iching.contentProviders.*
import java.io.Serializable
import java.util.*


data class LineText(
  /** 爻辭 */
  val expression: String,
  /** 象曰 */
  val image: String) : Serializable

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
interface IHexagramText : IHexagramName {
  /** 卦名 */
  val hexagramName : IHexagramName
  /** 卦辭 */
  val expression : String
  /** 象曰 */
  val image : String
  /** 彖曰 */
  val judgement : String
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
  override val extraLine: LineText?) : IHexagramText , IHexagramName by hexagramName , Serializable


interface IHexagramTextContext {
  fun getHexagramText(hexagram: IHexagram, locale: Locale?=Locale.getDefault()) : IHexagramText
}

class HexagramTextContext(private val hexagramNameFull: IHexagramNameFull ,
                          private val hexagramNameShort: IHexagramNameShort,
                          private val expressionImpl: IExpression ,
                          private val imageImpl: IImage ,
                          private val judgementImpl: IHexagramJudgement) : IHexagramTextContext , Serializable {
  override fun getHexagramText(hexagram: IHexagram, locale: Locale?): IHexagramText {

    val finalLocale = locale?:Locale.getDefault()

    val shortName = hexagramNameShort.getNameShort(hexagram, finalLocale)
    val fullName = hexagramNameFull.getNameFull(hexagram, finalLocale)
    val hexExpression = expressionImpl.getHexagramExpression(hexagram, finalLocale)
    val hexImage = imageImpl.getHexagramImage(hexagram, finalLocale)
    val hexJudgement = judgementImpl.getJudgement(hexagram, finalLocale)

    val lineTexts: List<LineText> = (1..6).map { lineIndex ->
      val lineExpression = expressionImpl.getLineExpression(hexagram, lineIndex, finalLocale)
      val lineImage = imageImpl.getLineImage(hexagram, lineIndex, finalLocale)
      LineText(lineExpression, lineImage)
    }.toList()

    val seq: IHexagramSequence = HexagramDefaultComparator()
    val extraLine: LineText? = seq.getIndex(hexagram).let {
      if (it == 1 || it == 2) {
        val lineExpression = expressionImpl.getExtraExpression(hexagram, finalLocale)
        val lineImage = imageImpl.getExtraImage(hexagram, finalLocale)
        LineText(lineExpression!!, lineImage!!)
      } else
        null
    }
    val hName = HexagramName(shortName , fullName)
    return HexagramText(hName, hexExpression, hexImage, hexJudgement, lineTexts, extraLine)
  }
}