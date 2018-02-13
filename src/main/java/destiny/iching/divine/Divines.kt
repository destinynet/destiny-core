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
import destiny.iching.Hexagram
import destiny.iching.Symbol
import destiny.iching.contentProviders.IHexagramNameFull
import java.time.chrono.ChronoLocalDateTime
import java.util.*

object Divines {

  fun getPlate(src: Hexagram,
               dst: Hexagram,
               hexagramNameFull: IHexagramNameFull,
               納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
               伏神系統: IHiddenEnergy = HiddenEnergyWangImpl()): DivinePlate {

    val srcNameFull = hexagramNameFull.getNameFull(src , Locale.TAIWAN)
    val dstNameFull = hexagramNameFull.getNameFull(dst , Locale.TAIWAN)
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

    return DivinePlate(src, dst,
                       srcNameFull , dstNameFull,
                       本宮, 變宮,
                       本卦宮序, 變卦宮序,
                       本卦世爻, 本卦應爻,
                       變卦世爻, 變卦應爻,
                       本卦納甲, 變卦納甲, 伏神納甲,
                       本卦六親, 變卦六親, 變卦對於本卦的六親,
                       伏神六親)
  }

  fun getFullPlate(src: Hexagram,
                   dst: Hexagram,
                   hexagramNameFull: IHexagramNameFull,
                   gender: Gender? = null,
                   question: String? = null,
                   approach: DivineApproach,
                   time: ChronoLocalDateTime<*>?,
                   loc: Location? = Location.of(Locale.TAIWAN),
                   place: String? = null,
                   eightWordsNullable: EightWordsNullable?,
                   納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                   伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                   tianyiImpl: ITianyi = TianyiAuthorizedImpl(),
                   yangBladeImpl: IYangBlade = YangBladeNextBlissImpl()): DivinePlateFull {


    val ewNullable = eightWordsNullable?: EightWordsNullable.empty()

    val day: StemBranch? = ewNullable.day.let { stemBranchOptional ->
      stemBranchOptional.stem?.let { stem -> stemBranchOptional.branch?.let { branch ->
        StemBranch[stem , branch]
      } }
    }

    val plate = getPlate(src, dst, hexagramNameFull, 納甲系統, 伏神系統)

    val 空亡: Set<Branch>? = day?.empties?.toSet()
    val 驛馬: Branch? = day?.branch?.let { Characters.getHorse(it) }
    val 桃花: Branch? =  day?.branch?.let { Characters.getPeach(it) }
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