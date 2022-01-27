/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.chinese.IYinYang
import destiny.core.chinese.ziwei.House.命宮
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 鄰宮起大限
 */
class BigRangeSkipMain : IBigRange,
                         Descriptive by BigRange.SkipMain.asDescriptive(),
                         Serializable {

  override fun getVageRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeqImpl: IHouseSeq): Pair<Int, Int> {
    return getAgeRange(house, set, yinYang, gender, houseSeqImpl)
  }

  /** 虛歲  */
  private fun getAgeRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeq: IHouseSeq): Pair<Int, Int> {
    val steps: Int = if (yinYang.booleanValue && gender === Gender.男 || !yinYang.booleanValue && gender === Gender.女) {
      // 陽男陰女順行
      houseSeq.getAheadOf(命宮.prev(1, houseSeq), house)
    } else {
      // 陰男陽女逆行
      houseSeq.getAheadOf(house, 命宮.next(1, houseSeq))
    }
    val fromRange = set + steps * 10
    val toRange = set + steps * 10 + 9
    return Pair(fromRange, toRange)
  }

}
