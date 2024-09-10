/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.IYinYang
import destiny.core.chinese.StemBranch
import destiny.tools.KotlinLogging

/** 起大限  */
interface IFlowSection : Descriptive {

  /** 取得此 house 的大限起訖時刻 , 歲數不考慮實歲或虛歲 , 皆為 inclusive */
  fun getAgeRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeqImpl: IHouseSeq): Pair<Int, Int>

  /** 計算每個地支 的 大限 起訖 歲數 (不考慮實歲或虛歲 )  */
  fun getFlowSectionAgeMap(branchHouseMap: Map<Branch, House>, set: Int, birthYear: StemBranch, gender: Gender, houseSeq: IHouseSeq): Map<Branch, Pair<Int, Int>> {
    return Branch.entries.associateWith { branch ->
      getAgeRange(branchHouseMap.getValue(branch), set, birthYear.stem, gender, houseSeq)
    }
  }

  /** 承上 , 計算每個地支的 大限 起訖 「歲數」時刻 (不考慮實歲或虛歲 )，並且按照先後順序排列 (年齡 小 -> 大)  */
  fun getSortedFlowSectionAgeMap(branchHouseMap: Map<Branch, House>, set: Int, birthYear: StemBranch, gender: Gender, houseSeq: IHouseSeq): Map<StemBranch, Pair<Int, Int>> {
    val map = getFlowSectionAgeMap(branchHouseMap, set, birthYear, gender, houseSeq)

    val stemOf寅 = Ziwei.getStemOf寅(birthYear.stem)

    return map.entries.map { entry ->
      val sb = Ziwei.getStemBranchOf(entry.key, stemOf寅)
      sb to entry.value
    }.sortedBy { (_, pair) -> pair.first }.toMap()
  }

  companion object {

    val logger = KotlinLogging.logger { }
  }
}
