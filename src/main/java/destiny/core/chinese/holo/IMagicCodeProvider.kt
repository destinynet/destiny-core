/**
 * Created by smallufo on 2019-04-08.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement

interface IMagicCodeProvider {

  /** 查本命 */
  fun getBaseText(fiveElement: FiveElement, day: Branch, hour: Branch, gender: Gender) : String

  /** 查歲運 */
  fun getFlowText(fiveElement: FiveElement , day:Branch , hour:Branch) : String
}