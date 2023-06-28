/**
 * Created by smallufo on 2018-06-30.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * 給 [StarLucky.天馬] 使用，可能用 年支 或 月支
 */
abstract class HouseYearMonthImpl internal constructor(star: ZStar) : HouseAbstractImpl<Pair<Branch, Branch>>(star) {

  override fun getBranch(t: Pair<Branch, Branch>): Branch {
    throw RuntimeException("error : $t")
  }
}