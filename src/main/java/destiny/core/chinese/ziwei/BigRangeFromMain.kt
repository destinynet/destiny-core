/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.IYinYang
import destiny.core.chinese.ziwei.House.命宮
import java.io.Serializable

/**
 * 「命宮起大限」
 *
 * 第一大限皆自命宮起，依照命盤局數來起。
 * 水二局，就是在命宮由二歲起大限，到十一歲截止。
 * 至於第二大限，就有順逆之分。陽男陰女順行、陰男陽女逆行。所以，
 *
 * 順行的話，就是命、父、福順時針而行。
 * 逆行就是命、兄、夫逆時針而行。
 */
class BigRangeFromMain : IBigRange, Serializable {

  override fun getVageRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeqImpl: IHouseSeq): Pair<Int, Int> {
    return getAgeRange(house, set, yinYang, gender, houseSeqImpl)
  }

  /** 虛歲  */
  private fun getAgeRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeq: IHouseSeq): Pair<Int, Int> {
    val steps: Int = if (yinYang.booleanValue && gender === Gender.男 || !yinYang.booleanValue && gender === Gender.女) {
      // 陽男陰女順行
      houseSeq.getAheadOf(命宮, house)
    } else {
      // 陰男陽女逆行
      houseSeq.getAheadOf(house, 命宮)
    }
    val fromRange = set + steps * 10
    val toRange = set + steps * 10 + 9
    return Pair(fromRange, toRange)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


}
