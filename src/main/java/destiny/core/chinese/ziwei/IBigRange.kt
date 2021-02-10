/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.IYinYang
import destiny.core.chinese.StemBranch
import mu.KotlinLogging
import java.util.*

/** 起大限  */
interface IBigRange : Descriptive {

  /** 取得此 house 的大限起訖時刻 , 傳回「虛歲」 (vAge)  */
  fun getVageRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeqImpl: IHouseSeq): Pair<Int, Int>

  /** 計算每個地支 的 大限 起訖 虛歲  */
  fun getFlowBigVageMap(branchHouseMap: Map<Branch, House>, set: Int, birthYear: StemBranch, gender: Gender, houseSeq: IHouseSeq): Map<Branch, Pair<Int, Int>> {
    return Branch.values().map { branch ->
      val pair = getVageRange(branchHouseMap.getValue(branch), set, birthYear.stem, gender, houseSeq)
      branch to pair
    }.toMap()
  }

  /** 承上 , 計算每個地支的 大限 起訖 「虛歲」時刻，並且按照先後順序排列 (年齡 小 -> 大)  */
  fun getSortedFlowBigVageMap(branchHouseMap: Map<Branch, House>, set: Int, birthYear: StemBranch, gender: Gender, houseSeq: IHouseSeq): Map<StemBranch, Pair<Int, Int>> {
    val map = getFlowBigVageMap(branchHouseMap, set, birthYear, gender, houseSeq)

    val stemOf寅 = Ziwei.getStemOf寅(birthYear.stem)

    return map.entries.map { entry ->
      val sb = Ziwei.getStemBranchOf(entry.key, stemOf寅)
      sb to entry.value
    }.sortedBy { (_, pair) -> pair.first }.toMap()
  }


  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IBigRange::class.qualifiedName!!, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }

  companion object {

    val logger = KotlinLogging.logger { }
  }
}
