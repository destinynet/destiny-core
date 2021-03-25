package destiny.core.iching.divine

import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.chinese.*
import destiny.core.iching.Hexagram
import destiny.core.iching.HexagramName
import destiny.core.iching.IHexagram
import destiny.core.iching.Symbol
import destiny.core.iching.contentProviders.IHexNameFull
import destiny.core.iching.contentProviders.IHexNameShort
import java.io.Serializable
import java.util.*


interface ISingleHexagramContext {
  /** 納甲系統 */
  val settings: ISettingsOfStemBranch
  /** 伏神系統 */
  val hiddenEnergy: IHiddenEnergy

  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  fun getSingleHexagram(hexagram: IHexagram): ISingleHexagram
}

interface ISingleHexagramWithNameContext : ISingleHexagramContext {
  val nameShortImpl: IHexNameShort
  val nameFullImpl: IHexNameFull
  fun getSingleHexagramWithName(hexagram: IHexagram, locale: Locale = Locale.TAIWAN): ISingleHexagramWithName
}

/** 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
interface ICombinedWithMetaContext : ISingleHexagramWithNameContext {
  fun getCombinedWithMeta(src: IHexagram,
                          dst: IHexagram,
                          locale: Locale = Locale.TAIWAN): ICombinedWithMeta
}


/** 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照) */
interface ICombinedWithMetaNameContext : ICombinedWithMetaContext {

  fun getCombinedWithMetaName(src: IHexagram,
                              dst: IHexagram,
                              locale: Locale = Locale.TAIWAN): ICombinedWithMetaName
}

/**
 * 具備「日干支」「月令」 , 可以排出六獸 [SixAnimal] 以及神煞 , 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] )
 * 通常用於 書籍、古書 當中卦象對照
 * */
interface ICombinedWithMetaNameDayMonthContext : ICombinedWithMetaNameContext {
  val tianyiImpl: ITianyi
  val yangBladeImpl: IYangBlade

  fun getCombinedWithMetaNameDayMonth(src: IHexagram,
                                      dst: IHexagram,
                                      eightWordsNullable: IEightWordsNullable,
                                      locale: Locale = Locale.TAIWAN): ICombinedWithMetaNameDayMonth
}

/**
 * 傳統、簡易版的排盤，只具備(可能不完整的)八字資料，不具備明確的日期
 */
class DivineTraditionalContext(override val settings: ISettingsOfStemBranch,
                               override val hiddenEnergy: IHiddenEnergy,
                               override val tianyiImpl: ITianyi,
                               override val yangBladeImpl: IYangBlade,
                               override val nameShortImpl: IHexNameShort,
                               override val nameFullImpl: IHexNameFull) : ICombinedWithMetaNameDayMonthContext, Serializable {

  /**
   * 世爻應爻
   * @param symbolSteps 宮序
   * */
  private fun getSelfOppo(symbolSteps: Int): Pair<Int, Int> = when (symbolSteps) {
    1 -> Pair(6, 3)
    2 -> Pair(1, 4)
    3 -> Pair(2, 5)
    4 -> Pair(3, 6)
    5 -> Pair(4, 1)
    6 -> Pair(5, 2)
    7 -> Pair(4, 1)
    8 -> Pair(3, 6)
    else -> throw IllegalArgumentException("impossible")
  }


  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  override fun getSingleHexagram(hexagram: IHexagram): ISingleHexagram {

    // 本宮 以及 宮序
    val (symbol, symbolSteps) = getSymbolAndIndex(hexagram)

    // 世爻, 應爻
    val (self, oppo) = getSelfOppo(symbolSteps)

    val 納甲: List<StemBranch> = (1..6).map { index -> settings.getStemBranch(hexagram, index) }.toList()

    val 本宮五行 = symbol.fiveElement

    // 六親
    val relatives: List<Relative> = (0..5).map { getRelative(SimpleBranch.getFiveElement(納甲[it].branch), 本宮五行) }.toList()

    val 伏神納甲: List<StemBranch?> = (1..6).map { index -> hiddenEnergy.getStemBranch(hexagram, settings, index) }.toList()

    val 伏神六親: List<Relative?> =
      伏神納甲.map { it?.let { sb -> getRelative(SimpleBranch.getFiveElement(sb.branch), 本宮五行) } }.toList()

    return SingleHexagram(hexagram, symbol, symbolSteps, self, oppo, 納甲, relatives, 伏神納甲, 伏神六親)
  }

  override fun getSingleHexagramWithName(hexagram: IHexagram,
                                         locale: Locale): ISingleHexagramWithName {
    val model = getSingleHexagram(hexagram)


    val nameShort = nameShortImpl.getHexagram(hexagram, locale)
    val nameFull = nameFullImpl.getHexagram(hexagram, locale)
    return SingleHexagramWithName(model, HexagramName(nameShort, nameFull))
  }


  /** [ICombinedWithMeta] 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
  override fun getCombinedWithMeta(src: IHexagram,
                                   dst: IHexagram,
                                   locale: Locale): ICombinedWithMeta {

    val meta = Meta(settings, hiddenEnergy)
    val srcModel = getSingleHexagram(src)
    val dstModel = getSingleHexagram(dst)

    val 變卦對於本卦的六親: List<Relative> =
      (0..5).map {
        getRelative(SimpleBranch.getFiveElement(dstModel.納甲[it].branch), srcModel.symbol.fiveElement)
      }.toList()
    return CombinedWithMeta(srcModel, dstModel, 變卦對於本卦的六親, meta)
  }

  /** [ICombinedWithMetaName] 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照) */
  override fun getCombinedWithMetaName(src: IHexagram,
                                       dst: IHexagram,
                                       locale: Locale): ICombinedWithMetaName {

    val srcModel = getSingleHexagramWithName(src, locale)
    val dstModel = getSingleHexagramWithName(dst, locale)
    val combined = getCombinedWithMeta(src, dst, locale)

    return CombinedWithMetaName(srcModel, dstModel, combined.變卦對於本卦的六親, Meta(combined.settings, combined.hiddenEnergy))
  }


  /**
   * 「可能」具備「日干支」「月令」 , 可以排出六獸 [SixAnimal] 以及神煞 ,
   * 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] )
   * 通常用於 書籍、古書 當中卦象對照
   * */
  override fun getCombinedWithMetaNameDayMonth(src: IHexagram,
                                               dst: IHexagram,
                                               eightWordsNullable: IEightWordsNullable,
                                               locale: Locale): ICombinedWithMetaNameDayMonth {

    val day: StemBranch? = eightWordsNullable.day.let {
      if (it.stem != null && it.branch != null)
        StemBranch[it.stem!!, it.branch!!]
      else
        null
    }

    val combinedWithMetaName = getCombinedWithMetaName(src, dst, locale)

    // ======== 神煞 ========

    // 空亡
    val voids: Set<Branch> = day?.empties?.toSet() ?: emptySet()
    // 驛馬
    val horse: Branch? = day?.branch?.let { Characters.getHorse(it) }
    // 桃花
    val flower: Branch? = day?.branch?.let { Characters.getPeach(it) }
    // 貴人
    val tianyis: Set<Branch> = day?.stem?.let { tianyiImpl.getTianyis(it).toSet() } ?: emptySet()
    // 羊刃
    val yangBlade: Branch? = day?.stem?.let { yangBladeImpl.getYangBlade(it) }
    // 六獸
    val sixAnimals: List<SixAnimal> = day?.let { SixAnimals.getSixAnimals(it.stem) } ?: emptyList()

    return CombinedWithMetaNameDayMonth(combinedWithMetaName, eightWordsNullable, voids, horse, flower, tianyis, yangBlade, sixAnimals)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DivineTraditionalContext) return false

    if (settings != other.settings) return false
    if (hiddenEnergy != other.hiddenEnergy) return false
    if (tianyiImpl != other.tianyiImpl) return false
    if (yangBladeImpl != other.yangBladeImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = settings.hashCode()
    result = 31 * result + hiddenEnergy.hashCode()
    result = 31 * result + tianyiImpl.hashCode()
    result = 31 * result + yangBladeImpl.hashCode()
    return result
  }


  companion object {
    val comparator = HexagramDivinationComparator()

    /** 取得此卦 是哪個本宮的第幾卦 (1~8) */
    fun getSymbolAndIndex(hexagram: IHexagram): Pair<Symbol, Int> {
      // 京房易卦卦序
      val hexIndex = comparator.getIndex(hexagram)

      /* 宮位 : 0乾 , 1兌 , 2離 , 3震 , 4巽 , 5坎 , 6艮 , 7坤 */
      val symbol = (hexIndex - 1) / 8

      // 宮序 : 1~8
      val index = hexIndex - symbol * 8

      // 本宮
      val srcSymbol: Symbol = Hexagram.of(symbol * 8 + 1, comparator).upperSymbol
      return srcSymbol to index
    }


    fun getRelative(outer: FiveElement, inner: FiveElement): Relative {
      return when {
        outer.isSame(inner) -> Relative.兄弟
        outer.isDominatorOf(inner) -> Relative.官鬼
        outer.isDominatedBy(inner) -> Relative.妻財
        outer.isProducingTo(inner) -> Relative.父母
        outer.isProducedBy(inner) -> Relative.子孫
        else -> throw RuntimeException("$outer and $inner")
      }
    }
  }
}
