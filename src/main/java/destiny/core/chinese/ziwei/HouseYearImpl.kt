/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch

/**
 * 年干支 ，用於旬空兩顆星
 * 必須判斷 [IZiweiContext.yearType]
 */
abstract class HouseYearImpl internal constructor(star: ZStar) : HouseAbstractImpl<StemBranch>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    return getBranch(context.year)
  }
}
