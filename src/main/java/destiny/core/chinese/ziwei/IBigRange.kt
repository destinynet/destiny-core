/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.IYinYang
import destiny.core.chinese.StemBranch
import org.slf4j.LoggerFactory
import java.util.*

/** 起大限  */
interface IBigRange : Descriptive {

  /** 取得此 house 的大限起訖時刻 , 傳回「虛歲」 (vAge)  */
  fun getVageRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeqImpl: IHouseSeq): Pair<Int, Int>

  /** 計算每個地支 的 大限 起訖 虛歲  */
  fun getFlowBigVageMap(branchHouseMap: Map<Branch, House>, set: Int, birthYear: StemBranch, gender: Gender, houseSeq: IHouseSeq): Map<Branch, Pair<Int, Int>> {
    return Branch.values().map { branch ->
      val pair = getVageRange(branchHouseMap[branch]!!, set, birthYear.stem, gender, houseSeq)
      branch to pair
    }.toMap()
  }

  /** 承上 , 計算每個地支的 大限 起訖 「虛歲」時刻，並且按照先後順序排列 (年齡 小 -> 大)  */
  fun getSortedFlowBigVageMap(branchHouseMap: Map<Branch, House>, set: Int, birthYear: StemBranch, gender: Gender, houseSeq: IHouseSeq): Map<StemBranch, Pair<Int, Int>> {
    val map = getFlowBigVageMap(branchHouseMap, set, birthYear, gender, houseSeq)

    val stemOf寅 = IZiwei.getStemOf寅(birthYear.stem)

    return map.entries.map { entry ->
      val sb = IZiwei.getStemBranchOf(entry.key, stemOf寅)
      sb to entry.value
    }.sortedBy { (_ , pair) -> pair.first  }.toMap()
  }


  override fun getTitle(locale: Locale): String {
    try {
      return ResourceBundle.getBundle(IBigRange::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      return javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }

  companion object {

    val logger = LoggerFactory.getLogger(IBigRange::class.java)
  }
}
