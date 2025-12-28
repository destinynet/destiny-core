/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.onePalm.PositiveImpl.GenderYinYang
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * http://curtisyen74.pixnet.net/blog/post/19456721
 * 陽男陰女，順時鐘方向數；陰男陽女，逆時鐘方向數。
 * 陽：民國年個位數為奇數。 (地支陽)
 * 陰：民國年個位數為偶數。 (地支陰)
 */
class PositiveGenderYinYangImpl : IPositive,
                                  Descriptive by GenderYinYang.asDescriptive(),
                                  Serializable {

  override fun isPositive(gender: Gender, yearBranch: Branch): Boolean {
    return gender === Gender.M && yearBranch.index % 2 == 0 || gender === Gender.F && yearBranch.index % 2 == 1
  }

}
