/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.ITianyi
import destiny.core.chinese.Stem

/**
 * (年干,天乙貴人設定) -> 地支
 * 適用於 [StarLucky.天魁] (陽貴人) , [StarLucky.天鉞] (陰貴人)
 * 2017-05-20 新增 [ZiweiConfig.yearType] 的判斷
 */
abstract class HouseYearStemTianyiImpl internal constructor(star: ZStar) : HouseAbstractImpl<Pair<Stem, ITianyi>>(star) {


  override fun getBranch(context: HouseCalContext): Branch {
    val yearStem = context.year.stem
    return getBranch(Pair(yearStem, context.tianyiImplMap[context.config.tianyi]!!))
  }
}
