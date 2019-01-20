/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

import destiny.core.Gender
import destiny.core.calendar.*
import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.chinese.*
import destiny.iching.*
import destiny.iching.contentProviders.*
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

interface ISingleHexagramContext {
  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  fun getSingleHexagram(hexagram: IHexagram,
                        納甲系統: ISettingsOfStemBranch? = null,
                        伏神系統: IHiddenEnergy? = null): ISingleHexagram
}

interface ISingleHexagramWithNameContext : ISingleHexagramContext {
  fun getSingleHexagramWithName(hexagram: IHexagram,
                                locale: Locale?=null,
                                納甲系統: ISettingsOfStemBranch? = null,
                                伏神系統: IHiddenEnergy? = null,
                                nameShortImpl: IHexagramNameShort? = null,
                                nameFullImpl: IHexagramNameFull? = null): ISingleHexagramWithName
}

/** 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
interface ICombinedWithMetaContext : ISingleHexagramWithNameContext {
  fun getCombinedWithMeta(src: IHexagram,
                          dst: IHexagram,
                          locale: Locale? = Locale.getDefault(),
                          納甲系統: ISettingsOfStemBranch? = null,
                          伏神系統: IHiddenEnergy? = null): ICombinedWithMeta
}


/** 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照) */
interface ICombinedWithMetaNameContext : ICombinedWithMetaContext {

  fun getCombinedWithMetaName(src: IHexagram,
                              dst: IHexagram,
                              locale: Locale? = Locale.getDefault(),
                              納甲系統: ISettingsOfStemBranch? = null,
                              伏神系統: IHiddenEnergy? = null ,
                              nameShortImpl: IHexagramNameShort?=null,
                              nameFullImpl: IHexagramNameFull?=null): ICombinedWithMetaName
}

/**
 * 具備「日干支」「月令」 , 可以排出六獸 [SixAnimal] 以及神煞 , 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] )
 * 通常用於 書籍、古書 當中卦象對照
 * */
interface ICombinedWithMetaNameDayMonthContext : ICombinedWithMetaNameContext {
  fun getCombinedWithMetaNameDayMonth(src: IHexagram,
                                      dst: IHexagram,
                                      eightWordsNullable: IEightWordsNullable,
                                      locale: Locale? = null,
                                      納甲系統: ISettingsOfStemBranch? = null,
                                      伏神系統: IHiddenEnergy? = null,
                                      tianyiImpl: ITianyi? = null,
                                      yangBladeImpl: IYangBlade? = null): ICombinedWithMetaNameDayMonth
}


/** 完整易卦排盤 , 包含時間、地點、八字、卦辭爻辭、神煞 等資料 */
interface ICombinedFullContext : ICombinedWithMetaNameDayMonthContext {

  fun getCombinedFull(src: IHexagram,
                      dst: IHexagram,
                      eightWordsNullable: IEightWordsNullable,
                      gender: Gender?,

                      question: String?,
                      approach: DivineApproach?,
                      lmt: ChronoLocalDateTime<*>?,
                      loc: ILocation? = Location.of(Locale.TAIWAN),

                      place: String? = null,
                      locale: Locale? = Locale.getDefault(),
                      納甲系統: ISettingsOfStemBranch? = null,

                      伏神系統: IHiddenEnergy? = null,
                      tianyiImpl: ITianyi? = null,
                      yangBladeImpl: IYangBlade? = null,

                      expressionImpl: IExpression? = null,
                      imageImpl: IImage? = null,
                      judgementImpl: IHexagramJudgement? = null): ICombinedFull

}

class DivineContext(
  val 納甲系統: ISettingsOfStemBranch,
  val 伏神系統: IHiddenEnergy,
  private val locale: Locale,
  private val nameShortImpl: IHexagramNameShort,
  private val nameFullImpl: IHexagramNameFull,
  val tianyiImpl: ITianyi,
  val yangBladeImpl: IYangBlade,
  private val expressionImpl: IExpression,
  private val imageImpl: IImage,
  private val judgementImpl: IHexagramJudgement)
  : ISingleHexagramContext, ISingleHexagramWithNameContext, ICombinedWithMetaContext, ICombinedWithMetaNameContext,
  ICombinedWithMetaNameDayMonthContext, ICombinedFullContext, Serializable {


  val comparator = HexagramDivinationComparator()

  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  override fun getSingleHexagram(hexagram: IHexagram,
                                 納甲系統: ISettingsOfStemBranch?,
                                 伏神系統: IHiddenEnergy?): ISingleHexagram {
    val final納甲 = 納甲系統 ?: this.納甲系統
    val final伏神 = 伏神系統 ?: this.伏神系統

    val 京房易卦卦序 = comparator.getIndex(hexagram)

    /* 0乾 , 1兌 , 2離 , 3震 , 4巽 , 5坎 , 6艮 , 7坤 */
    val 宮位 = (京房易卦卦序 - 1) / 8

    // 1~8
    val 宮序 = 京房易卦卦序 - 宮位 * 8

    val 本宮: Symbol = Hexagram.getHexagram(宮位 * 8 + 1, comparator).upperSymbol

    val (世爻, 應爻) = get世爻應爻(宮序)

    val 納甲: List<StemBranch> = (1..6).map { index -> final納甲.getStemBranch(hexagram, index) }.toList()

    val 本宮五行 = 本宮.fiveElement

    val 六親: List<Relative> = (0..5).map { getRelative(SimpleBranch.getFiveElement(納甲[it].branch), 本宮五行) }.toList()

    val 伏神納甲: List<StemBranch?> = (1..6).map { index -> final伏神.getStemBranch(hexagram, final納甲, index) }.toList()

    val 伏神六親: List<Relative?> =
      伏神納甲.map { it?.let { sb -> getRelative(SimpleBranch.getFiveElement(sb.branch), 本宮五行) } }.toList()

    return SingleHexagram(hexagram, 本宮, 宮序, 世爻, 應爻, 納甲, 六親, 伏神納甲, 伏神六親)
  }

  override fun getSingleHexagramWithName(hexagram: IHexagram,
                                         locale: Locale?,
                                         納甲系統: ISettingsOfStemBranch?,
                                         伏神系統: IHiddenEnergy? ,
                                         nameShortImpl: IHexagramNameShort?,
                                         nameFullImpl: IHexagramNameFull?): ISingleHexagramWithName {
    val model = getSingleHexagram(hexagram, 納甲系統, 伏神系統)

    val finalLocale = locale?:this.locale
    val finalNameShortImpl = nameShortImpl?:this.nameShortImpl
    val finalNameFullImpl = nameFullImpl?:this.nameFullImpl

    val nameShort = finalNameShortImpl.getNameShort(hexagram , finalLocale)
    val nameFull = finalNameFullImpl.getNameFull(hexagram , finalLocale)
    return SingleHexagramWithName(model , HexagramName(nameShort , nameFull))
  }

  /** [ICombinedWithMeta] 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
  override fun getCombinedWithMeta(src: IHexagram,
                                   dst: IHexagram,
                                   locale: Locale?,
                                   納甲系統: ISettingsOfStemBranch?,
                                   伏神系統: IHiddenEnergy?): ICombinedWithMeta {
    val final納甲 = 納甲系統 ?: this.納甲系統
    val final伏神 = 伏神系統 ?: this.伏神系統

    val meta = Meta(final納甲, final伏神)
    val srcModel = getSingleHexagram(src, 納甲系統, 伏神系統)
    val dstModel = getSingleHexagram(dst, 納甲系統, 伏神系統)

    val 變卦對於本卦的六親: List<Relative> =
      (0..5).map {
        getRelative(SimpleBranch.getFiveElement(dstModel.納甲[it].branch), srcModel.symbol.fiveElement)
      }.toList()
    return CombinedWithMeta(srcModel, dstModel, 變卦對於本卦的六親, meta)
  }

  /** [ICombinedWithMetaName] 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照) */
  override fun getCombinedWithMetaName(src: IHexagram,
                                       dst: IHexagram,
                                       locale: Locale?,
                                       納甲系統: ISettingsOfStemBranch?,
                                       伏神系統: IHiddenEnergy? ,
                                       nameShortImpl: IHexagramNameShort?,
                                       nameFullImpl: IHexagramNameFull?): ICombinedWithMetaName {

    val srcModel = getSingleHexagramWithName(src , locale, 納甲系統, 伏神系統, nameShortImpl, nameFullImpl)
    val dstModel = getSingleHexagramWithName(dst , locale, 納甲系統, 伏神系統, nameShortImpl, nameFullImpl)
    val combined = getCombinedWithMeta(src, dst, locale, 納甲系統, 伏神系統)

    return CombinedWithMetaName(srcModel, dstModel, combined.變卦對於本卦的六親, Meta(combined.納甲系統, combined.伏神系統))
  }


  /**
   * 具備「日干支」「月令」 , 可以排出六獸 [SixAnimal] 以及神煞 , 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] )
   * 通常用於 書籍、古書 當中卦象對照
   * */
  override fun getCombinedWithMetaNameDayMonth(src: IHexagram,
                                               dst: IHexagram,
                                               eightWordsNullable: IEightWordsNullable,
                                               locale: Locale?,
                                               納甲系統: ISettingsOfStemBranch?,
                                               伏神系統: IHiddenEnergy?,
                                               tianyiImpl: ITianyi?,
                                               yangBladeImpl: IYangBlade?): ICombinedWithMetaNameDayMonth {

    val finalTianyi = tianyiImpl ?: this.tianyiImpl
    val finalYangBlade = yangBladeImpl ?: this.yangBladeImpl

    val day: StemBranch = eightWordsNullable.day.let { StemBranch[it.stem!!, it.branch!!] }

    val combinedWithMetaName = getCombinedWithMetaName(src, dst, locale, 納甲系統, 伏神系統)

    // 神煞
    val 空亡: Set<Branch> = day.empties.toSet()
    val 驛馬: Branch = day.branch.let { Characters.getHorse(it) }
    val 桃花: Branch = day.branch.let { Characters.getPeach(it) }
    val 貴人: Set<Branch> = day.stem.let { finalTianyi.getTianyis(it).toSet() }
    val 羊刃: Branch = day.stem.let { finalYangBlade.getYangBlade(it) }
    val 六獸: List<SixAnimal> = day.let { SixAnimals.getSixAnimals(it.stem) }

    return CombinedWithMetaNameDayMonth(combinedWithMetaName, eightWordsNullable, 空亡, 驛馬, 桃花, 貴人, 羊刃, 六獸)
  }


  /** 完整易卦排盤 , 包含時間、地點、八字、卦辭爻辭、神煞 等資料 */
  override fun getCombinedFull(src: IHexagram,
                               dst: IHexagram,
                               eightWordsNullable: IEightWordsNullable,
                               gender: Gender?,
                               question: String?,
                               approach: DivineApproach?,
                               lmt: ChronoLocalDateTime<*>?,
                               loc: ILocation?,
                               place: String?,

                               locale: Locale?,
                               納甲系統: ISettingsOfStemBranch?,
                               伏神系統: IHiddenEnergy?,
                               tianyiImpl: ITianyi?,
                               yangBladeImpl: IYangBlade?,
                               expressionImpl: IExpression?,
                               imageImpl: IImage?,
                               judgementImpl: IHexagramJudgement?): ICombinedFull {

    val combinedWithMetaNameDayMonth = getCombinedWithMetaNameDayMonth(src, dst, eightWordsNullable, locale, 納甲系統,
                                                                       伏神系統, tianyiImpl, yangBladeImpl)
    val gmtJulDay: Double? = lmt?.let { TimeTools.getGmtJulDay(it, loc!!) }

    val decoratedDate = lmt?.let { DateDecorator.getOutputString(it.toLocalDate() , Locale.TAIWAN) }
    val decoratedDateTime = lmt?.let { TimeSecDecorator.getOutputString(it, Locale.TAIWAN) }

    val meta = Meta(combinedWithMetaNameDayMonth.納甲系統, combinedWithMetaNameDayMonth.伏神系統)
    val divineMeta = DivineMeta(gender, question, approach, gmtJulDay, loc, place,
                                decoratedDate, decoratedDateTime, meta , null)

    val finalExpressionImpl = expressionImpl ?: this.expressionImpl
    val finalImageImpl = imageImpl ?: this.imageImpl
    val finalJudgeImpl = judgementImpl ?: this.judgementImpl

    val textContext: IHexagramTextContext =
      HexagramTextContext(nameFullImpl, nameShortImpl, finalExpressionImpl , finalImageImpl, finalJudgeImpl)
    val srcText = textContext.getHexagramText(src, locale)
    val dstText = textContext.getHexagramText(dst, locale)

    val pairTexts: Pair<IHexagramText, IHexagramText> = Pair(srcText, dstText)
    return CombinedFull(combinedWithMetaNameDayMonth, eightWordsNullable, divineMeta, pairTexts)
  }

  private fun get世爻應爻(宮序: Int): Pair<Int, Int> = when (宮序) {
    1 -> Pair(6, 3)
    2 -> Pair(1, 4)
    3 -> Pair(2, 5)
    4 -> Pair(3, 6)
    5 -> Pair(4, 1)
    6 -> Pair(5, 2)
    7 -> Pair(4, 1)
    8 -> Pair(3, 6)
    else -> throw RuntimeException("impossible")
  }


  companion object {
    fun getRelative(外在五行: FiveElement, 內在五行: FiveElement): Relative {
      return when {
        外在五行.equals(內在五行) -> Relative.兄弟
        外在五行.isDominatorOf(內在五行) -> Relative.官鬼
        外在五行.isDominatedBy(內在五行) -> Relative.妻財
        外在五行.isProducingTo(內在五行) -> Relative.父母
        外在五行.isProducedBy(內在五行) -> Relative.子孫
        else -> throw RuntimeException("$外在五行 and $內在五行")
      }
    }

  } // companion


} // DivineContext
