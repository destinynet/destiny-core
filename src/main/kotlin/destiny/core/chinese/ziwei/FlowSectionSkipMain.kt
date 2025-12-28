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
class FlowSectionSkipMain : IFlowSection,
                            Descriptive by BigRange.SkipMain.asDescriptive(),
                            Serializable {

  override fun getAgeRange(house: House, set: Int, yinYang: IYinYang, gender: Gender, houseSeqImpl: IHouseSeq): Pair<Int, Int> {
    val steps: Int = if (yinYang.booleanValue && gender === Gender.M || !yinYang.booleanValue && gender === Gender.F) {
      // 陽男陰女順行
      houseSeqImpl.getAheadOf(命宮.prev(1, houseSeqImpl), house)
    } else {
      // 陰男陽女逆行
      houseSeqImpl.getAheadOf(house, 命宮.next(1, houseSeqImpl))
    }
    val fromRange = set + steps * 10
    val toRange = set + steps * 10 + 9
    return Pair(fromRange, toRange)
  }
}
