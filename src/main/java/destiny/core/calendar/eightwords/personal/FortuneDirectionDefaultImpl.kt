/*
 * @author smallufo
 * @date 2005/5/17
 * @time 上午 07:49:08
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender

import java.io.Serializable

/**
 * 大運的順逆，內定演算法：陽男陰女順行；陰男陽女逆行
 */
class FortuneDirectionDefaultImpl : IFortuneDirection, Serializable {

  /** 大運的順逆，內定演算法：陽男陰女順行；陰男陽女逆行  */
  override fun isForward(personContext: PersonContext): Boolean {
    return personContext.gender === Gender.男 && personContext.eightWords.year.stem.booleanValue
      || personContext.gender === Gender.女 && !personContext.eightWords.year.stem.booleanValue
  }

}
