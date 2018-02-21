/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

import destiny.core.chinese.FiveElement
import destiny.core.chinese.SimpleBranch
import destiny.core.chinese.StemBranch
import destiny.iching.Hexagram
import destiny.iching.HexagramName
import destiny.iching.IHexagram
import destiny.iching.Symbol
import destiny.iching.contentProviders.IHexagramNameFull
import destiny.iching.contentProviders.IHexagramNameShort
import java.util.*

object Divines {

  /** 單一卦象 ,（不含任何文字）的排卦結果 [SinglePlate] */
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

  /** 單一卦象 , 包含 長卦名 短卦名 等文字資料 [SinglePlateWithName] */
  fun getSinglePlateWithName(plate: ISinglePlate,
                             nameShortImpl: IHexagramNameShort,
                             nameFullImpl: IHexagramNameFull,
                             locale: Locale = Locale.TAIWAN): ISinglePlateWithName {
    val nameShort = nameShortImpl.getNameShort(plate.hexagram, locale)
    val nameFull = nameFullImpl.getNameFull(plate.hexagram, locale)
    val hexagramName = HexagramName(nameShort, nameFull)
    return SinglePlateWithName(plate as SinglePlate, hexagramName)
  }

  fun getSinglePlateWithName(hexagram: IHexagram,
                             納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                             伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                             nameShortImpl: IHexagramNameShort,
                             nameFullImpl: IHexagramNameFull,
                             locale: Locale = Locale.TAIWAN): ISinglePlateWithName {
    val singlePlate: SinglePlate = getSinglePlate(hexagram, 納甲系統, 伏神系統) as SinglePlate
    val nameShort = nameShortImpl.getNameShort(hexagram, locale)
    val nameFull = nameFullImpl.getNameFull(hexagram, locale)

    val hexagramName = HexagramName(nameShort, nameFull)
    return SinglePlateWithName(singlePlate, hexagramName)
  }


  fun getCombinedWithMetaNamePlate(src: IHexagram,
                                   dst: IHexagram,
                                   納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                                   伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                                   locale: Locale = Locale.TAIWAN,
                                   nameFullImpl: IHexagramNameFull,
                                   nameShortImpl: IHexagramNameShort): ICombinedWithMetaName {
    val srcPlate = getSinglePlateWithName(src , 納甲系統 , 伏神系統, nameShortImpl, nameFullImpl) as SinglePlateWithName
    val dstPlate = getSinglePlateWithName(dst , 納甲系統 , 伏神系統, nameShortImpl, nameFullImpl) as SinglePlateWithName
    val 變卦對於本卦的六親: List<Relative> =
      (0..5).map {
        Divines.getRelative(SimpleBranch.getFiveElement(dstPlate.納甲[it].branch), srcPlate.symbol.fiveElement)
      }.toList()

    return CombinedWithMetaName(srcPlate ,dstPlate , 變卦對於本卦的六親 , Meta(納甲系統.getTitle(locale), 伏神系統.getTitle(locale)))
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

  internal fun getRelative(外在五行: FiveElement, 內在五行: FiveElement): Relative {
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