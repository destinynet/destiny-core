/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.寅
import destiny.tools.asDescriptive
import mu.KotlinLogging
import java.io.Serializable

/**
 * 內定求紫微星的地支宮位，不考慮閏月問題
 *
 * 取得「紫微星」的「地支」
 *
 * 得出命造五行局後，推判幾倍的命造五行局數可以大於生日數
 * （例如：十六日生人木三局者則六倍，商數+1,得可大與生日數）；
 * 下一步判斷得出來的倍數與生日數之差數（(商數+1）*五行局數-生日數)，再判斷此差數為奇數或偶數；
 * 若差數為奇數，則以倍數減去差數得到一個新的數字；
 * 若差數為偶數，則倍數與差數相加而得一新的數字，
 * 下一步起寅宮並順時針數到上一步驟得出的數目，此一落宮點便是紫微星的位置；
 */
class PurpleStarBranchDefaultImpl : IPurpleStarBranch,
                                    Descriptive by PurpleStarBranch.Default.asDescriptive(),
                                    Serializable {

  /**
   * @param state 局數
   * @param day 該月第幾天
   * @param leap 是否是閏月 (此實作用不到)
   * @param prevMonthDays 前一月有幾日 (此實作用不到)
   */
  override fun getBranchOfPurpleStar(state: Int, day: Int, leap: Boolean, prevMonthDays: Int, ziweiForcedBranch: Branch?): Branch {
    return ziweiForcedBranch?: run {
      val steps = getPurpleSteps(state, day)
      寅.next(steps - 1)
    }
  }

  /**
   * 從「寅宮」，「順數」幾步到「紫微星」？
   * 也相等於：
   * 從「寅宮」，「逆數」幾步到「天府星」？
   */
  private fun getPurpleSteps(set: Int, day: Int): Int {
    var multiple = day / set
    logger.debug("{} / {} = {}", day, set, multiple)
    if (day % set > 0) {
      multiple++
      logger.debug("multiple ++ , new multiple = {}", multiple)
    }

    // 差數
    val diff = multiple * set - day

    return if (diff % 2 == 1) {
      // 奇數
      multiple - diff
    } else {
      // 偶數
      multiple + diff
    }
  }



  companion object {

    private val logger = KotlinLogging.logger { }
  }

}
