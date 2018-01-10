/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.chinese.Branch
import java.io.Serializable
import java.util.*

/**
 * http://curtisyen74.pixnet.net/blog/post/19456721
 * 陽男陰女，順時鐘方向數；陰男陽女，逆時鐘方向數。
 * 陽：民國年個位數為奇數。 (地支陽)
 * 陰：民國年個位數為偶數。 (地支陰)
 */
class PositiveGenderYinYangImpl : IPositive, Serializable {

  override fun isPositive(gender: Gender, yearBranch: Branch): Boolean {
    return gender === Gender.男 && yearBranch.index % 2 == 0 || gender === Gender.女 && yearBranch.index % 2 == 1
  }

  override fun getTitle(locale: Locale): String {
    return "陽男陰女順；陰男陽女逆"
  }

  override fun getDescription(locale: Locale): String {
    return "陽男陰女順；陰男陽女逆。"

  }

}
