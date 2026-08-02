/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.tools.asDescriptive
import destiny.tools.JSerializable

class PositiveGenderImpl : IPositive,
                           Descriptive by PositiveImpl.Gender.asDescriptive(),
                           JSerializable {

  override fun isPositive(gender: Gender, yearBranch: Branch): Boolean {
    return gender === Gender.M
  }
}
