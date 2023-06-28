/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * (日數,時支) -> 地支
 */
abstract class HouseDayNumHourBranchImpl internal constructor(star: ZStar) :
  HouseAbstractImpl<Pair<Int, Branch>>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    return getBranch(Pair(context.days, context.hour))
  }
}
