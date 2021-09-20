/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.onePalm.PalmConfig.PositiveImpl.GenderYinYang
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Palm.KEY_POSITIVE_IMPL
import java.io.Serializable
import java.util.*

/**
 * http://curtisyen74.pixnet.net/blog/post/19456721
 * 陽男陰女，順時鐘方向數；陰男陽女，逆時鐘方向數。
 * 陽：民國年個位數為奇數。 (地支陽)
 * 陰：民國年個位數為偶數。 (地支陰)
 */
@Impl([Domain(KEY_POSITIVE_IMPL , PositiveGenderYinYangImpl.VALUE)])
class PositiveGenderYinYangImpl : IPositive, Serializable {

  override fun isPositive(gender: Gender, yearBranch: Branch): Boolean {
    return gender === Gender.男 && yearBranch.index % 2 == 0 || gender === Gender.女 && yearBranch.index % 2 == 1
  }

  override fun toString(locale: Locale): String {
    return GenderYinYang.asDescriptive().toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return GenderYinYang.asDescriptive().getDescription(locale)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    const val VALUE = "gyy"
  }
}
