/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * 時支 -> 地支
 */
abstract class HouseHourBranchImpl internal constructor(star: ZStar) : HouseAbstractImpl<Branch>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    return getBranch(context.hour)
  }
}
