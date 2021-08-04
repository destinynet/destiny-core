/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.core.iching.divine

import destiny.core.Gender
import destiny.core.calendar.*
import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.iching.HexagramTextContext
import destiny.core.iching.IHexagram
import destiny.core.iching.IHexagramText
import destiny.core.iching.contentProviders.IHexJudgement
import destiny.core.iching.contentProviders.IHexagramExpression
import destiny.core.iching.contentProviders.IHexagramImage
import destiny.core.iching.contentProviders.IHexagramProvider
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/** 完整易卦排盤 , 包含時間、地點、八字、卦辭爻辭、神煞 等資料 */
interface ICombinedFullContext : ICombinedWithMetaNameDayMonthContext {
  val hexExpressionImpl: IHexagramExpression
  val hexImageImpl: IHexagramImage
  val hexJudgement: IHexJudgement

  /** 完整易卦排盤 , 包含時間、地點、八字(可能不完整)、卦辭爻辭、神煞 等資料 */
  fun getCombinedFull(src: IHexagram,
                      dst: IHexagram,
                      eightWordsNullable: IEightWordsNullable,
                      gender: Gender?,

                      question: String?,
                      approach: DivineApproach?,
                      lmt: ChronoLocalDateTime<*>?,
                      loc: ILocation = locationOf(Locale.TAIWAN),

                      place: String? = null,
                      locale: Locale = Locale.TAIWAN): ICombinedFull

}

class DivineContext(
  private val traditionalContext: ICombinedWithMetaNameDayMonthContext,
  override val hexExpressionImpl: IHexagramExpression,
  override val hexImageImpl: IHexagramImage,
  override val hexJudgement: IHexJudgement
) : ICombinedFullContext, ICombinedWithMetaNameDayMonthContext by traditionalContext , Serializable {



  /** 完整易卦排盤 , 包含時間、地點、八字(可能不完整)、卦辭爻辭、神煞 等資料 */
  override fun getCombinedFull(src: IHexagram,
                               dst: IHexagram,
                               eightWordsNullable: IEightWordsNullable,
                               gender: Gender?,
                               question: String?,
                               approach: DivineApproach?,
                               lmt: ChronoLocalDateTime<*>?,
                               loc: ILocation,
                               place: String?,
                               locale: Locale
  ): ICombinedFull {

    val combinedWithMetaNameDayMonth = getCombinedWithMetaNameDayMonth(src, dst, eightWordsNullable, locale)
    val gmtJulDay: GmtJulDay? = lmt?.let { TimeTools.getGmtJulDay(it, loc) }

    logger.debug("eightWordsNullable = {}", eightWordsNullable)
    val decoratedDate = lmt?.let { DateDecorator.getOutputString(it.toLocalDate(), Locale.TAIWAN) }
    val decoratedDateTime = lmt?.let { TimeSecDecorator.getOutputString(it, Locale.TAIWAN) }

    val meta = Meta(combinedWithMetaNameDayMonth.settings, combinedWithMetaNameDayMonth.hiddenEnergy)
    val divineMeta = DivineMeta(gender, question, approach, gmtJulDay, loc, place,
      decoratedDate, decoratedDateTime, meta, null)

    val textContext: IHexagramProvider<IHexagramText> =
      HexagramTextContext(nameFullImpl, nameShortImpl, hexExpressionImpl, hexImageImpl, hexJudgement)
    val srcText = textContext.getHexagram(src, locale)
    val dstText = textContext.getHexagram(dst, locale)

    val pairTexts: Pair<IHexagramText, IHexagramText> = Pair(srcText, dstText)
    return CombinedFull(combinedWithMetaNameDayMonth, eightWordsNullable, divineMeta, pairTexts)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DivineContext) return false

    if (traditionalContext != other.traditionalContext) return false

    return true
  }

  override fun hashCode(): Int {
    return traditionalContext.hashCode()
  }


  companion object {
    val logger = KotlinLogging.logger { }
  } // companion


} // DivineContext
