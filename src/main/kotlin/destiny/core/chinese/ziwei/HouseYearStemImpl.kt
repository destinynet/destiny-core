/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem

/**
 * 年干 系星
 * 必須判斷 [IZiweiConfig.yearType]
 */
abstract class HouseYearStemImpl internal constructor(star: ZStar) : HouseAbstractImpl<Stem>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    val yearStem = context.year.stem
    return getBranch(yearStem)
  }
}
