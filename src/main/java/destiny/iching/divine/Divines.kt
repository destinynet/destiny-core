/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.Location
import destiny.core.calendar.TimeSecDecorator
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IEightWords
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

/** 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
interface ICombinedWithMetaContext : ISingleHexagramContext {
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
                              伏神系統: IHiddenEnergy? = null): ICombinedWithMetaName
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
                      eightWordsNullable: IEightWords,
                      gender: Gender?,

                      question: String?,
                      approach: DivineApproach,
                      time: ChronoLocalDateTime<*>?,
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
  private val 納甲系統: ISettingsOfStemBranch,
  private val 伏神系統: IHiddenEnergy,
  private val locale: Locale,
  private val nameShortImpl: IHexagramNameShort,
  private val nameFullImpl: IHexagramNameFull,
  private val tianyiImpl: ITianyi,
  private val yangBladeImpl: IYangBlade,
  private val expressionImpl: IExpression,
  private val imageImpl: IImage,
  private val judgementImpl: IHexagramJudgement)
  : ISingleHexagramContext, ICombinedWithMetaContext, ICombinedWithMetaNameContext,
  ICombinedWithMetaNameDayMonthContext, ICombinedFullContext , Serializable {


  val comparator = HexagramDivinationComparator()

  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  override fun getSingleHexagram(hexagram: IHexagram,
                                 納甲系統: ISettingsOfStemBranch?,
                                 伏神系統: IHiddenEnergy?): ISingleHexagram {
    val final納甲 = 納甲系統 ?: this.納甲系統
    val final伏神 = 伏神系統 ?: this.伏神系統

//    println("\n納甲系統 from param = $納甲系統")
//    println("納甲系統 from this = ${this.納甲系統}")
//    println("final納甲 = $final納甲")

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

  /** [ICombinedWithMeta] 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
  override fun getCombinedWithMeta(src: IHexagram,
                                   dst: IHexagram,
                                   locale: Locale?,
                                   納甲系統: ISettingsOfStemBranch?,
                                   伏神系統: IHiddenEnergy?): ICombinedWithMeta {
    val final納甲 = 納甲系統 ?: this.納甲系統
    val final伏神 = 伏神系統 ?: this.伏神系統
    val finalLocale = locale ?: this.locale

    val meta = Meta(final納甲.getTitle(finalLocale), final伏神.getTitle(finalLocale))
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
                                       伏神系統: IHiddenEnergy?): ICombinedWithMetaName {

    val finalLocale = locale ?: this.locale
    val srcHex = getSingleHexagram(src, 納甲系統, 伏神系統)
    val srcNameShort = nameShortImpl.getNameShort(src, finalLocale)
    val srcNameFull = nameFullImpl.getNameFull(src, finalLocale)
    val srcModel = SingleHexagramWithName(srcHex, HexagramName(srcNameShort, srcNameFull))

    val dstHex = getSingleHexagram(dst, 納甲系統, 伏神系統)
    val dstNameShort = nameShortImpl.getNameShort(dst, finalLocale)
    val dstNameFull = nameFullImpl.getNameFull(dst, finalLocale)
    val dstModel = SingleHexagramWithName(dstHex, HexagramName(dstNameShort, dstNameFull))

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
                               eightWords: IEightWords,
                               gender: Gender?,
                               question: String?,
                               approach: DivineApproach,
                               time: ChronoLocalDateTime<*>?,
                               loc: ILocation?,
                               place: String?,

                               locale: Locale?,
                               納甲系統: ISettingsOfStemBranch?,
                               伏神系統: IHiddenEnergy?,
                               tianyiImpl: ITianyi?,
                               yangBladeImpl: IYangBlade?,
                               expressionImpl: IExpression?,
                               imageImpl: IImage?,
                               judgementImpl: IHexagramJudgement?) : ICombinedFull {

    val combinedWithMetaNameDayMonth = getCombinedWithMetaNameDayMonth(src, dst, eightWords, locale, 納甲系統,
                                                                       伏神系統, tianyiImpl, yangBladeImpl)
    val gmtJulDay: Double? = time?.let { TimeTools.getGmtJulDay(it, loc!!) }
    val decoratedTime = time?.let { TimeSecDecorator.getOutputString(it, Locale.TAIWAN) }

    val meta = Meta(combinedWithMetaNameDayMonth.納甲系統 , combinedWithMetaNameDayMonth.伏神系統)
    val divineMeta = DivineMeta(gender, question, approach, gmtJulDay, loc, place, decoratedTime, meta, null)

    val finalExpressionImpl = expressionImpl?:this.expressionImpl
    val finalImageImpl = imageImpl?:this.imageImpl
    val finalJudgeImpl = judgementImpl?:this.judgementImpl

    val textContext : IHexagramTextContext = HexagramTextContext(nameFullImpl , nameShortImpl , finalExpressionImpl , finalImageImpl, finalJudgeImpl)
    val srcText = textContext.getHexagramText(src , locale)
    val dstText = textContext.getHexagramText(dst , locale)

    val pairTexts: Pair<IHexagramText, IHexagramText> = Pair(srcText, dstText)
    return CombinedFull(combinedWithMetaNameDayMonth, eightWords, divineMeta, pairTexts)
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

} // DivineContext


object Divines {

  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  @Deprecated("DivineContext")
  fun getSinglePlate(hexagram: IHexagram,
                     納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                     伏神系統: IHiddenEnergy = HiddenEnergyWangImpl()): ISingleHexagram {
    //val hex = Hexagram.getHexagram(hexagram)
    val comparator = HexagramDivinationComparator()
    val 京房易卦卦序 = comparator.getIndex(hexagram)

    /* 0乾 , 1兌 , 2離 , 3震 , 4巽 , 5坎 , 6艮 , 7坤 */
    val 宮位 = (京房易卦卦序 - 1) / 8

    // 1~8
    val 宮序 = 京房易卦卦序 - 宮位 * 8

    val 本宮: Symbol = Hexagram.getHexagram(宮位 * 8 + 1, comparator).upperSymbol

    val (世爻, 應爻) = get世爻應爻(宮序)

    val 納甲: List<StemBranch> = (1..6).map { index -> 納甲系統.getStemBranch(hexagram, index) }.toList()

    val 本宮五行 = 本宮.fiveElement

    val 六親: List<Relative> = (0..5).map { getRelative(SimpleBranch.getFiveElement(納甲[it].branch), 本宮五行) }.toList()

    val 伏神納甲: List<StemBranch?> = (1..6).map { index -> 伏神系統.getStemBranch(hexagram, 納甲系統, index) }.toList()

    val 伏神六親: List<Relative?> =
      伏神納甲.map { it?.let { sb -> getRelative(SimpleBranch.getFiveElement(sb.branch), 本宮五行) } }.toList()

    return SingleHexagram(hexagram, 本宮, 宮序, 世爻, 應爻, 納甲, 六親, 伏神納甲, 伏神六親)
  }

  /** 單一卦象 , 包含 長卦名 短卦名 等文字資料 [SingleHexagramWithName] */
  @Deprecated("DivineContext")
  fun getSinglePlateWithName(hexagram: ISingleHexagram,
                             nameShortImpl: IHexagramNameShort,
                             nameFullImpl: IHexagramNameFull,
                             locale: Locale = Locale.TAIWAN): ISingleHexagramWithName {
    val nameShort = nameShortImpl.getNameShort(hexagram.hexagram, locale)
    val nameFull = nameFullImpl.getNameFull(hexagram.hexagram, locale)
    val hexagramName = HexagramName(nameShort, nameFull)
    return SingleHexagramWithName(hexagram as SingleHexagram, hexagramName)
  }

  fun getSinglePlateWithName(hexagram: IHexagram,
                             納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                             伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                             nameShortImpl: IHexagramNameShort,
                             nameFullImpl: IHexagramNameFull,
                             locale: Locale = Locale.TAIWAN): ISingleHexagramWithName {
    val singlePlate: SingleHexagram = getSinglePlate(hexagram, 納甲系統, 伏神系統) as SingleHexagram
    val nameShort = nameShortImpl.getNameShort(hexagram, locale)
    val nameFull = nameFullImpl.getNameFull(hexagram, locale)

    val hexagramName = HexagramName(nameShort, nameFull)
    return SingleHexagramWithName(singlePlate, hexagramName)
  }


  fun getCombinedWithMetaNamePlate(src: IHexagram,
                                   dst: IHexagram,
                                   納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                                   伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                                   locale: Locale = Locale.TAIWAN,
                                   nameFullImpl: IHexagramNameFull,
                                   nameShortImpl: IHexagramNameShort): ICombinedWithMetaName {
    val srcPlate = getSinglePlateWithName(src, 納甲系統, 伏神系統, nameShortImpl, nameFullImpl) as SingleHexagramWithName
    val dstPlate = getSinglePlateWithName(dst, 納甲系統, 伏神系統, nameShortImpl, nameFullImpl) as SingleHexagramWithName
    val 變卦對於本卦的六親: List<Relative> =
      (0..5).map {
        Divines.getRelative(SimpleBranch.getFiveElement(dstPlate.納甲[it].branch), srcPlate.symbol.fiveElement)
      }.toList()

    return CombinedWithMetaName(srcPlate, dstPlate, 變卦對於本卦的六親, Meta(納甲系統.getTitle(locale), 伏神系統.getTitle(locale)))
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
}