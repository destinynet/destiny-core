/*
 * @author smallufo
 * @date 2005/5/17
 * @time 上午 07:49:08
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IEightWordsFactory
import java.io.Serializable

/**
 * 大運的順逆，內定演算法：陽男陰女順行；陰男陽女逆行
 */
class FortuneDirectionDefaultImpl(val eightWordsImpl: IEightWordsFactory) : IFortuneDirection, Serializable {

  override fun isForward(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender): Boolean {
    val eightWords = eightWordsImpl.getEightWords(gmtJulDay, loc)

    return gender === Gender.男 && eightWords.year.stem.booleanValue
      || gender === Gender.女 && !eightWords.year.stem.booleanValue
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FortuneDirectionDefaultImpl) return false

    if (eightWordsImpl != other.eightWordsImpl) return false

    return true
  }

  override fun hashCode(): Int {
    return eightWordsImpl.hashCode()
  }


}
