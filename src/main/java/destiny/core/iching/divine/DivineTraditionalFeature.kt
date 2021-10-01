/**
 * Created by smallufo on 2021-09-26.
 */
package destiny.core.iching.divine

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.chinese.*
import destiny.core.iching.Hexagram
import destiny.core.iching.HexagramName
import destiny.core.iching.IHexagram
import destiny.core.iching.Symbol
import destiny.core.iching.contentProviders.IHexNameFull
import destiny.core.iching.contentProviders.IHexNameShort
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import destiny.tools.serializers.IHexagramSerializer
import destiny.tools.serializers.LocaleSerializer
import kotlinx.serialization.Serializable
import java.util.*
import javax.inject.Named

@Serializable
data class DivineTraditionalConfig(@Serializable(with = IHexagramSerializer::class)
                                   val src: IHexagram = Hexagram.乾,
                                   @Serializable(with = IHexagramSerializer::class)
                                   val dst: IHexagram = Hexagram.乾,
                                   val settings: SettingsOfStemBranch = SettingsOfStemBranch.GingFang,
                                   val hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang,
                                   val tianyi: Tianyi = Tianyi.Authorized,
                                   val yangBlade: YangBlade = YangBlade.NextBliss,
                                   @Serializable(with = LocaleSerializer::class)
                                   val locale: Locale = Locale.TAIWAN,
                                   val eightWordsConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable
@DestinyMarker
class DivineTraditionalConfigBuilder : Builder<DivineTraditionalConfig> {

  var src: IHexagram = Hexagram.乾

  var dst: IHexagram = Hexagram.乾

  var settings: SettingsOfStemBranch = SettingsOfStemBranch.GingFang

  var hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang

  var tianyi: Tianyi = Tianyi.Authorized

  var yangBlade: YangBlade = YangBlade.NextBliss

  var locale: Locale = Locale.TAIWAN

  var eightWordsConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}) {
    this.eightWordsConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  override fun build(): DivineTraditionalConfig {
    return DivineTraditionalConfig(src, dst, settings, hiddenEnergy, tianyi, yangBlade, locale, eightWordsConfig)
  }

  companion object {
    fun divineTraditionalConfig(block: DivineTraditionalConfigBuilder.() -> Unit = {}) : DivineTraditionalConfig {
      return DivineTraditionalConfigBuilder().apply(block).build()
    }
  }
}


/**
 * 傳統、簡易版的排盤，只具備(可能不完整的)八字資料，不具備明確的日期
 */
interface IDivineTraditionalFeature : Feature<DivineTraditionalConfig, ICombinedWithMetaNameDayMonth>, ICombinedWithMetaNameDayMonthContext

@Named
class DivineTraditionalFeature(private val eightWordsFeature: EightWordsFeature,
                               private val divineSettingsMap: Map<SettingsOfStemBranch, ISettingsOfStemBranch>,
                               private val hiddenEnergyMap: Map<HiddenEnergy, IHiddenEnergy>,
                               private val tianyiImplMap: Map<Tianyi, ITianyi>,
                               private val yangBladeImplMap: Map<YangBlade, IYangBlade>,
                               private val nameShortImpl: IHexNameShort,
                               private val nameFullImpl: IHexNameFull) : AbstractCachedFeature<DivineTraditionalConfig , ICombinedWithMetaNameDayMonth>(), IDivineTraditionalFeature {

  override val key: String = "divineTraditionalFeature"

  override val defaultConfig: DivineTraditionalConfig = DivineTraditionalConfig()


  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  override fun getSingleHexagram(hexagram: IHexagram, settings: SettingsOfStemBranch, hiddenEnergy: HiddenEnergy): ISingleHexagram {
    // 本宮 以及 宮序
    val (symbol, symbolSteps) = getSymbolAndIndex(hexagram)

    // 世爻, 應爻
    val (self, oppo) = getSelfOppo(symbolSteps)

    val settingsImpl = divineSettingsMap[settings]!!

    val 納甲: List<StemBranch> = (1..6).map { index -> settingsImpl.getStemBranch(hexagram, index) }.toList()

    val 本宮五行 = symbol.fiveElement

    // 六親
    val relatives: List<Relative> = (0..5).map { getRelative(SimpleBranch.getFiveElement(納甲[it].branch), 本宮五行) }.toList()

    val 伏神納甲: List<StemBranch?> = (1..6).map { index -> hiddenEnergyMap[hiddenEnergy]!!.getStemBranch(hexagram, settingsImpl, index) }.toList()

    val 伏神六親: List<Relative?> =
      伏神納甲.map { it?.let { sb -> getRelative(SimpleBranch.getFiveElement(sb.branch), 本宮五行) } }.toList()

    return SingleHexagram(hexagram, symbol, symbolSteps, self, oppo, 納甲, relatives, 伏神納甲, 伏神六親)
  }

  override fun getSingleHexagramWithName(hexagram: IHexagram, settings: SettingsOfStemBranch, hiddenEnergy: HiddenEnergy, locale: Locale): ISingleHexagramWithName {

    val model = getSingleHexagram(hexagram, settings, hiddenEnergy)

    val nameShort = nameShortImpl.getHexagram(hexagram, locale)
    val nameFull = nameFullImpl.getHexagram(hexagram, locale)
    return SingleHexagramWithName(model, HexagramName(nameShort, nameFull))
  }

  override fun getCombinedDivine(src: IHexagram, dst: IHexagram, settings: SettingsOfStemBranch, hiddenEnergy: HiddenEnergy): ICombinedWithMeta {

    val srcModel = getSingleHexagram(src, settings, hiddenEnergy)
    val dstModel = getSingleHexagram(dst, settings, hiddenEnergy)

    val 變卦對於本卦的六親: List<Relative> =
      (0..5).map { getRelative(SimpleBranch.getFiveElement(dstModel.納甲[it].branch), srcModel.symbol.fiveElement) }.toList()

    val meta = Meta(settings, hiddenEnergy)

    return CombinedWithMeta(srcModel, dstModel, 變卦對於本卦的六親, meta)
  }


  override fun getCombinedWithMetaName(src: IHexagram, dst: IHexagram, settings: SettingsOfStemBranch, hiddenEnergy: HiddenEnergy, locale: Locale): ICombinedWithMetaName {
    val srcModel = getSingleHexagramWithName(src, settings, hiddenEnergy, locale)
    val dstModel = getSingleHexagramWithName(dst, settings, hiddenEnergy, locale)
    val combined = getCombinedDivine(src, dst, settings, hiddenEnergy)

    return CombinedWithMetaName(srcModel, dstModel, combined.變卦對於本卦的六親, Meta(settings, hiddenEnergy))
  }

  override fun getCombinedWithMetaNameDayMonth(eightWordsNullable: IEightWordsNullable, config: DivineTraditionalConfig): ICombinedWithMetaNameDayMonth {
    val day: StemBranch? = eightWordsNullable.day.let {
      if (it.stem != null && it.branch != null)
        StemBranch[it.stem!!, it.branch!!]
      else
        null
    }

    val combinedWithMetaName = getCombinedWithMetaName(config.src, config.dst, config.settings, config.hiddenEnergy, config.locale)

    // ======== 神煞 ========

    // 空亡
    val voids: Set<Branch> = day?.empties?.toSet() ?: emptySet()
    // 驛馬
    val horse: Branch? = day?.branch?.let { Characters.getHorse(it) }
    // 桃花
    val flower: Branch? = day?.branch?.let { Characters.getPeach(it) }
    // 貴人
    val tianyis: Set<Branch> = day?.stem?.let { tianyiImplMap[config.tianyi]!!.getTianyis(it).toSet() } ?: emptySet()
    // 羊刃
    val yangBlade: Branch? = day?.stem?.let { yangBladeImplMap[config.yangBlade]!!.getYangBlade(it) }
    // 六獸
    val sixAnimals: List<SixAnimal> = day?.let { SixAnimals.getSixAnimals(it.stem) } ?: emptyList()


    return CombinedWithMetaNameDayMonth(combinedWithMetaName, eightWordsNullable, voids, horse, flower, tianyis, yangBlade, sixAnimals)
  }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: DivineTraditionalConfig): ICombinedWithMetaNameDayMonth {

    val ew = eightWordsFeature.getModel(gmtJulDay, loc, config.eightWordsConfig)

    return getCombinedWithMetaNameDayMonth(ew, config)
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

    /**
     * 世爻應爻
     * @param symbolSteps 宮序
     * */
    fun getSelfOppo(symbolSteps: Int): Pair<Int, Int> = when (symbolSteps) {
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
  }
}
