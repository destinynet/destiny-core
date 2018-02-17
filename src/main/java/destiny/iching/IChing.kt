/**
 * Created by smallufo on 2018-02-17.
 */
package destiny.iching

import destiny.iching.contentProviders.*
import java.util.*

object IChing {

  fun getHexagramText(hexagram: IHexagram,
                      locale: Locale,
                      hexagramNameFull: IHexagramNameFull,
                      hexagramNameShort: IHexagramNameShort,
                      expressionImpl: IExpression,
                      imageImpl: IImage,
                      judgementImpl: IHexagramJudgement): HexagramText {
    val shortName = hexagramNameShort.getNameShort(hexagram, locale)
    val fullName = hexagramNameFull.getNameFull(hexagram, locale)
    val hexExpression = expressionImpl.getHexagramExpression(hexagram, locale)
    val hexImage = imageImpl.getHexagramImage(hexagram, locale)
    val hexJudgement = judgementImpl.getJudgement(hexagram, locale)

    val lineTexts: List<LineText> = (1..6).map { lineIndex ->
      val lineExpression = expressionImpl.getLineExpression(hexagram, lineIndex, locale)
      val lineImage = imageImpl.getLineImage(hexagram, lineIndex, locale)
      LineText(lineExpression, lineImage)
    }.toList()

    val seq: IHexagramSequence = HexagramDefaultComparator()
    val extraLine: LineText? = seq.getIndex(hexagram).let {
      if (it == 1 || it == 2) {
        val lineExpression = expressionImpl.getExtraExpression(hexagram, locale)
        val lineImage = imageImpl.getExtraImage(hexagram, locale)
        LineText(lineExpression, lineImage)
      } else
        null
    }
    val hName = HexagramName(shortName , fullName)
    return HexagramText(hName, hexExpression, hexImage, hexJudgement, lineTexts, extraLine)
  }
}