/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 *
 * (年支,時支) -> 地支
 *
 * [HouseFunctions.house火星] [HouseFunctions.house鈴星]  使用
 */
abstract class HouseYearBranchHourBranchImpl internal constructor(star: ZStar) : HouseAbstractImpl<Pair<Branch, Branch>>(star) {

  override fun getBranch(objects: Pair<Branch, Branch>): Branch {
    throw RuntimeException("error : " + objects)
  }


}
