/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.子
import destiny.core.chinese.Stem.*

object StemBranchUtils {

  /**
   * 五虎遁年起月(干)
   */
  fun getMonthStem(yearStem: Stem, monthBranch: Branch): Stem {
    return Stem[yearStem.index % 5 * 2 + if (monthBranch.index >= 2) monthBranch.index else monthBranch.index + 12]
  }

  /**
   * 五鼠遁日起時(干)
   */
  fun getHourStem(dayStem: Stem, hourBranch: Branch): Stem {
    // return Stem.get((dayStem.getIndex() % 5) *2 + hourBranch.getIndex());
    // 以下的解法，不用 expose index
    return when (dayStem) {
      甲, 己 -> StemBranch[甲, 子].next(hourBranch.getAheadOf(子)).stem
      乙, 庚 -> StemBranch[丙, 子].next(hourBranch.getAheadOf(子)).stem
      丙, 辛 -> StemBranch[戊, 子].next(hourBranch.getAheadOf(子)).stem
      丁, 壬 -> StemBranch[庚, 子].next(hourBranch.getAheadOf(子)).stem
      戊, 癸 -> StemBranch[壬, 子].next(hourBranch.getAheadOf(子)).stem
    }
  }
}
