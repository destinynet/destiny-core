/**
 * Created by smallufo on 2017-12-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 *
 * 歲前12星 [StarYearFront]
 *
 * 年支 -> 地支
 * 其中的「年支」，可能是陰曆、也可能是節氣
 * 鍾義明 的書籍特別提出，年系星，應該用立春分界 , 參考截圖 http://imgur.com/WVUxCc8
 */
class HouseStarYearFrontImpl(star: StarYearFront) : HouseAbstractImpl<Branch>(star) {

  override fun getBranch(t: Branch): Branch {
    return StarYearFront.starFuncMap[star]!!.invoke(t)
  }

  override fun getBranch(context: HouseCalContext): Branch {
    val yearBranch = context.year.branch
    return getBranch(yearBranch)
  }

}
