/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.chinese.Branch
import java.io.Serializable
import java.util.*

class PositiveGenderImpl : IPositive, Serializable {

  override fun isPositive(gender: Gender, yearBranch: Branch): Boolean {
    return gender === Gender.男
  }

  override fun getTitle(locale: Locale): String {
    return "男順女逆"
  }

  override fun getDescription(locale: Locale): String {
    return "固定男順女逆"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

}
