/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * (月數,日數) -> 地支
 */
abstract class HouseMonthDayNumImpl internal constructor(star: ZStar) : HouseAbstractImpl<Pair<Int, Int>>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    return getBranch(Pair(context.finalMonthNumForMonthStars, context.days))
  }

}
