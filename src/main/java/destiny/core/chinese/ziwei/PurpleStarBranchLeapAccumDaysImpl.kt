/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.tools.asDescriptive
import mu.KotlinLogging
import java.io.Serializable

/**
 * 閏月排紫微星 , 累加前一月(非閏月)的日數
 *
 * 翰學居士 張寶丹 老師 , 高段紫微斗數 三本書
 *
 * 參考此表格 :  http://imgur.com/87sHQOq
 */
class PurpleStarBranchLeapAccumDaysImpl : IPurpleStarBranch,
                                          Descriptive by PurpleStarBranch.LeapAccumDays.asDescriptive(),
                                          Serializable {

  /**
   * @param state 局數
   * @param day 該月第幾天
   * @param leap 是否是閏月
   * @param prevMonthDays 前一月有幾日
   */
  override fun getBranchOfPurpleStar(state: Int, day: Int, leap: Boolean, prevMonthDays: Int): Branch {
    require(day + prevMonthDays > 30) {
      logger.error("日數 = {} , 加上前一個月的天數 {}  , 小於 30 日，不適用此 「日數累加推算紫微」演算法", day, prevMonthDays)
      "Error : 局數 = $state , day = $day , 閏月 = $leap , 前一個月日數 = $prevMonthDays"
    }

    return if (!leap) {
      getBranchOfPurpleStarNonLeap(state, day)
    } else {
      // 閏月
      // 取得新的日數
      val newDays = prevMonthDays + day
      when (state) {
        2 -> water2(newDays)
        3 -> wood3(newDays)
        4 -> gold4(newDays)
        5 -> earth5(newDays)
        6 -> fire6(newDays)
        else -> throw AssertionError("Error state : $state")
      }
    } // 閏月
  }

  /** 水二局  */
  internal fun water2(newDays: Int): Branch {
    if (newDays == 31)
      return 辰
    val branchIndex = (newDays - 32) / 2 % 12 + 5
    return Branch[branchIndex]
  }

  /** 木三局  */
  private fun wood3(newDays: Int): Branch {
    return woodMap.getValue(newDays)
  }

  /** 金四局  */
  private fun gold4(newDays: Int): Branch {
    return goldMap.getValue(newDays)
  }

  /** 土五局  */
  private fun earth5(newDays: Int): Branch {
    return earthMap.getValue(newDays)
  }

  /** 火六局  */
  private fun fire6(newDays: Int): Branch {
    return fireMap.getValue(newDays)
  }

  companion object {

    private val logger = KotlinLogging.logger { }

    private val woodMap = mapOf(
      31 to 寅,
      32 to 亥,
      33 to 子,
      34 to 卯,
      35 to 子,
      36 to 丑,
      37 to 辰,
      38 to 丑,
      39 to 寅,
      40 to 巳,
      41 to 寅,
      42 to 卯,
      43 to 午,
      44 to 卯,
      45 to 辰,
      46 to 未,
      47 to 辰,
      48 to 巳,
      49 to 申,
      50 to 巳,
      51 to 午,
      52 to 酉,
      53 to 午,
      54 to 未,
      55 to 戌,
      56 to 未,
      57 to 申,
      58 to 亥,
      59 to 申,
      60 to 酉
    )
    private val goldMap = mapOf(
      31 to 申,
      32 to 酉,
      33 to 未,
      34 to 子,
      35 to 酉,
      36 to 戌,
      37 to 申,
      38 to 丑,
      39 to 戌,
      40 to 亥,
      41 to 酉,
      42 to 寅,
      43 to 亥,
      44 to 子,
      45 to 戌,
      46 to 卯,
      47 to 子,
      48 to 丑,
      49 to 亥,
      50 to 辰,
      51 to 丑,
      52 to 寅,
      53 to 子,
      54 to 巳,
      55 to 寅,
      56 to 卯,
      57 to 丑,
      58 to 午,
      59 to 卯,
      60 to 辰
    )
    private val earthMap = mapOf(
      31 to 子,
      32 to 巳,
      33 to 戌,
      34 to 未,
      35 to 申,
      36 to 丑,
      37 to 午,
      38 to 亥,
      39 to 申,
      40 to 酉,
      41 to 寅,
      42 to 未,
      43 to 子,
      44 to 酉,
      45 to 戌,
      46 to 卯,
      47 to 申,
      48 to 丑,
      49 to 戌,
      50 to 亥,
      51 to 辰,
      52 to 酉,
      53 to 寅,
      54 to 亥,
      55 to 子,
      56 to 巳,
      57 to 戌,
      58 to 卯,
      59 to 子,
      60 to 丑
    )
    private val fireMap = mapOf(
      31 to 寅,
      32 to 亥,
      33 to 辰,
      34 to 酉,
      35 to 午,
      36 to 未,
      37 to 卯,
      38 to 子,
      39 to 巳,
      40 to 戌,
      41 to 未,
      42 to 申,
      43 to 辰,
      44 to 丑,
      45 to 午,
      46 to 亥,
      47 to 申,
      48 to 酉,
      49 to 巳,
      50 to 寅,
      51 to 未,
      52 to 子,
      53 to 酉,
      54 to 戌,
      55 to 午,
      56 to 卯,
      57 to 申,
      58 to 丑,
      59 to 戌,
      60 to 亥
    )
  }

}
