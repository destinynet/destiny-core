/**
 * Created by smallufo on 2019-04-08.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.IFiveElement

/** 金鎖銀匙歌訣 */
interface IGoldenKeyProvider {

  /** 查本命 */
  fun getBaseGoldenKey(fiveElement: IFiveElement, day: Branch, hour: Branch, gender: Gender) : GoldenKey?

  /** 查歲運 */
  fun getFlowGoldenKey(fiveElement: IFiveElement, day:Branch, flowBranch:Branch) : GoldenKey?
}