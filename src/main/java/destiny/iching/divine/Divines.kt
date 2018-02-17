/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

import destiny.core.Gender
import destiny.core.calendar.Location
import destiny.core.calendar.TimeSecDecorator
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsNullable
import destiny.core.chinese.*
import destiny.core.chinese.impls.TianyiAuthorizedImpl
import destiny.core.chinese.impls.YangBladeNextBlissImpl
import destiny.iching.*
import destiny.iching.contentProviders.*
import java.time.chrono.ChronoLocalDateTime
import java.util.*

object Divines {

  /** 取得單一卦象（不含任何文字）的排卦結果 */
  fun getSinglePlate(hexagram: IHexagram,
                     納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                     伏神系統: IHiddenEnergy = HiddenEnergyWangImpl()): ISinglePlate {
    val hex = Hexagram.getHexagram(hexagram)
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

    return SinglePlate(hex, 本宮, 宮序, 世爻, 應爻, 納甲, 六親, 伏神納甲, 伏神六親)
  }

  /** 包含 長卦名 短卦名 等文字資料 */
  fun getSinglePlateWithNames(hexagram: IHexagram,
                              納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                              伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                              nameShortImpl: IHexagramNameShort,
                              nameFullImpl: IHexagramNameFull,
                              locale: Locale = Locale.TAIWAN): ISinglePlateWithNames {
    val singlePlate: SinglePlate = getSinglePlate(hexagram, 納甲系統, 伏神系統) as SinglePlate
    val nameShort = nameShortImpl.getNameShort(hexagram, locale)
    val nameFull = nameFullImpl.getNameFull(hexagram, locale)

    val hexagramName = HexagramName(nameShort, nameFull)
    return SinglePlateWithNames(singlePlate, hexagramName)
  }

  /** 不傳回文字 */
  fun getPlate(src: IHexagram,
               dst: IHexagram,
               納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
               伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
               nameFullImpl: IHexagramNameFull,
               nameShortImpl: IHexagramNameShort): DivinePlate {
    return getPlate(src, dst, 納甲系統, 伏神系統, nameFullImpl, nameShortImpl, null, null, null)
  }

  fun getPlate(src: IHexagram,
               dst: IHexagram,
               納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
               伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
               nameFullImpl: IHexagramNameFull,
               nameShortImpl: IHexagramNameShort,
               expressionImpl: IExpression? = null,
               imageImpl: IImage? = null,
               judgementImpl: IHexagramJudgement? = null,
               textLocale: Locale? = null): DivinePlate {

    val srcNameFull = nameFullImpl.getNameFull(src, Locale.TAIWAN)
    val dstNameFull = nameFullImpl.getNameFull(dst, Locale.TAIWAN)
    val comparator = HexagramDivinationComparator()

    /* 1 <= 卦序 <= 64 */
    val 本卦京房易卦卦序 = comparator.getIndex(src)
    val 變卦京房易卦卦序 = comparator.getIndex(dst)

    /* 0乾 , 1兌 , 2離 , 3震 , 4巽 , 5坎 , 6艮 , 7坤 */
    val 本卦宮位 = (本卦京房易卦卦序 - 1) / 8
    val 變卦宮位 = (變卦京房易卦卦序 - 1) / 8

    // 1~8
    val 本卦宮序 = 本卦京房易卦卦序 - 本卦宮位 * 8
    val 變卦宮序 = 變卦京房易卦卦序 - 變卦宮位 * 8

    val 本宮: Symbol = Hexagram.getHexagram(本卦宮位 * 8 + 1, comparator).upperSymbol
    val 變宮: Symbol = Hexagram.getHexagram(變卦宮位 * 8 + 1, comparator).upperSymbol

    val (本卦世爻, 本卦應爻) = get世爻應爻(本卦宮序)
    val (變卦世爻, 變卦應爻) = get世爻應爻(變卦宮序)

    val 本卦納甲: List<StemBranch> = (1..6).map { index -> 納甲系統.getStemBranch(src, index) }.toList()
    val 變卦納甲: List<StemBranch> = (1..6).map { index -> 納甲系統.getStemBranch(dst, index) }.toList()
    val 伏神納甲: List<StemBranch?> = (1..6).map { index -> 伏神系統.getStemBranch(src, 納甲系統, index) }.toList()

    val 本宮五行 = 本宮.fiveElement
    val 變宮五行 = 變宮.fiveElement

    val 本卦六親: List<Relative> = (0..5).map { getRelative(SimpleBranch.getFiveElement(本卦納甲[it].branch), 本宮五行) }.toList()
    val 變卦六親: List<Relative> = (0..5).map { getRelative(SimpleBranch.getFiveElement(變卦納甲[it].branch), 變宮五行) }.toList()
    val 變卦對於本卦的六親: List<Relative> =
      (0..5).map { getRelative(SimpleBranch.getFiveElement(變卦納甲[it].branch), 本宮五行) }.toList()

    val 伏神六親: List<Relative?> =
      伏神納甲.map { it?.let { sb -> getRelative(SimpleBranch.getFiveElement(sb.branch), 本宮五行) } }.toList()

    val meta = Meta(納甲系統.getTitle(Locale.TAIWAN), 伏神系統.getTitle(Locale.TAIWAN))

    val srcNameShort: String = nameShortImpl.getNameShort(src, Locale.TAIWAN)
    val dstNameShort: String = nameShortImpl.getNameShort(dst, Locale.TAIWAN)

    val pairTexts: Pair<HexagramText, HexagramText>? =
      if (expressionImpl != null && imageImpl != null && judgementImpl != null && textLocale != null) {
        val srcText =
          IChing.getHexagramText(src, textLocale, nameFullImpl, nameShortImpl, expressionImpl, imageImpl, judgementImpl)
        val dstText =
          IChing.getHexagramText(dst, textLocale, nameFullImpl, nameShortImpl, expressionImpl, imageImpl, judgementImpl)
        Pair(srcText, dstText)
      } else
        null


    val s: Hexagram = Hexagram.getHexagram(src)
    val d: Hexagram = Hexagram.getHexagram(dst)


    return DivinePlate(Hexagram.getHexagram(src), Hexagram.getHexagram(dst), meta,
                       srcNameFull, dstNameFull, srcNameShort, dstNameShort,
                       本宮, 變宮,
                       本卦宮序, 變卦宮序,
                       本卦世爻, 本卦應爻,
                       變卦世爻, 變卦應爻,
                       本卦納甲, 變卦納甲, 本卦六親,
                       變卦六親, 變卦對於本卦的六親, 伏神納甲,
                       伏神六親, pairTexts)
  }

  fun getFullPlate(src: IHexagram,
                   dst: IHexagram,
                   hexagramNameFull: IHexagramNameFull,
                   hexagramNameShort: IHexagramNameShort,
                   expressionImpl: IExpression,
                   imageImpl: IImage,
                   judgementImpl: IHexagramJudgement,
                   gender: Gender? = null,
                   question: String? = null,
                   approach: DivineApproach?,
                   time: ChronoLocalDateTime<*>?,
                   loc: Location? = Location.of(Locale.TAIWAN),
                   place: String? = null,
                   eightWordsNullable: EightWordsNullable?,
                   納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                   伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                   tianyiImpl: ITianyi = TianyiAuthorizedImpl(),
                   yangBladeImpl: IYangBlade = YangBladeNextBlissImpl(),
                   textLocale: Locale? = null): DivinePlateFull {


    val ewNullable = eightWordsNullable ?: EightWordsNullable.empty()

    val day: StemBranch? = ewNullable.day.let { stemBranchOptional ->
      stemBranchOptional.stem?.let { stem ->
        stemBranchOptional.branch?.let { branch ->
          StemBranch[stem, branch]
        }
      }
    }

    val plate = getPlate(src, dst, 納甲系統, 伏神系統, hexagramNameFull, hexagramNameShort, expressionImpl, imageImpl,
                         judgementImpl, textLocale)

    val 空亡: Set<Branch>? = day?.empties?.toSet()
    val 驛馬: Branch? = day?.branch?.let { Characters.getHorse(it) }
    val 桃花: Branch? = day?.branch?.let { Characters.getPeach(it) }
    val 貴人: Set<Branch>? = day?.stem?.let { tianyiImpl.getTianyis(it).toSet() }
    val 羊刃: Branch? = day?.stem?.let { yangBladeImpl.getYangBlade(it) }
    val 六獸: List<SixAnimal>? = day?.let { SixAnimals.getSixAnimals(it.stem) }


    val gmtJulDay: Double? = time?.let { TimeTools.getGmtJulDay(it, loc) }
    val decoratedTime = time?.let { TimeSecDecorator.getOutputString(it, Locale.TAIWAN) }
    val meta = DivineMeta(gender, question, approach, gmtJulDay, loc, place,
                          decoratedTime, 納甲系統.getTitle(Locale.TAIWAN), 伏神系統.getTitle(Locale.TAIWAN), null)


    return DivinePlateFull(plate, meta, ewNullable, 空亡, 驛馬, 桃花, 貴人, 羊刃, 六獸)
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

  private fun getRelative(外在五行: FiveElement, 內在五行: FiveElement): Relative {
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