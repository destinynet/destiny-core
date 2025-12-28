/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.Gender.M
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*

/** 起小限  */
interface ISmallRange {
  companion object {

    /** 取得此地支宮位的小限列表
     *
     * 小限一律男命順行，女命逆行，而不分陰陽。
     * 凡屬
     * 寅午戌年生人，由辰宮起一歲小限，
     * 申子辰年生人，由戌宮起一歲小限，
     * 亥卯未年生人，由丑宮起一歲小限，
     * 巳酉丑年生人，由未宮起一歲小限。
     *
     * 這禮的歲是指虛歲。小限每十二年轉完命盤一周，又回到原來的宮位。
     *
     *
     * https://goo.gl/jlXXnN :
     *
     * 依命造的出生年支作判斷，以地支三合區分為四組，也就是申子辰、巳酉丑、寅午戌、亥卯未四組。
     * 例如申年出生的人，就屬於申子辰這一組；午年出生，則屬於寅午戌。
     * 每一組最後一個地支就是四墓庫，即辰、戌、丑、未四字。
     *
     * 小限排盤簡單的方法，就是以自己出生年那組的墓支對宮起一歲，男順行、女逆行，逐年逐宮去排列。
     * 舉例：如果命造為寅年之男命，其屬於寅午戌這組。
     * 要起該命造之小限盤，就從寅午戌的戌宮之對宮，
     * 也就是辰宮起一歲、二歲在巳宮（男命順行）、三歲在午宮，如此順排下去。
     */
    fun getRanges(house: Branch, birthYear: Branch, gender: Gender): List<Int> {
      val start = getStartingHouse(birthYear)
      val steps = if (gender === M) house.getAheadOf(start) else start.getAheadOf(house)

      val startAge = 1 + steps

      return (0..7).map { startAge + it * 12 }
    }

    /** 哪一宮 起 一歲小限  */
    private fun getStartingHouse(birthYear: Branch): Branch {
      return when (birthYear) {
        寅, 午, 戌 -> 辰
        申, 子, 辰 -> 戌
        亥, 卯, 未 -> 丑
        巳, 酉, 丑 -> 未
      }
    }
  }
}
