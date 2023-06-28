/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * 年支 -> 地支
 * 其中的「年支」，可能是陰曆、也可能是節氣
 * 鍾義明 的書籍特別提出，年系星，應該用立春分界 , 參考截圖 http://imgur.com/WVUxCc8
 */
abstract class HouseYearBranchImpl internal constructor(star: ZStar) : HouseAbstractImpl<Branch>(star) {

  override fun getBranch(context: HouseCalContext): Branch {
    val yearBranch = context.year.branch
    return getBranch(yearBranch)
  }
}
