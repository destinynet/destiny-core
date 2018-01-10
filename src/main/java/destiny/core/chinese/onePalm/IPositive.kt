/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.chinese.Branch

interface IPositive : Descriptive {

  fun isPositive(gender: Gender, yearBranch: Branch): Boolean
}
