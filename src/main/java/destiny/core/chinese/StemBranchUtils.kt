/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.chinese

object StemBranchUtils {

  /**
   * 五虎遁年起月(干)
   */
  fun getMonthStem(yearStem: Stem, monthBranch: Branch): Stem {
    return Stem.get(yearStem.index % 5 * 2 + if (monthBranch.index >= 2) monthBranch.index else monthBranch.index + 12)
  }

  /**
   * 五鼠遁日起時(干)
   */
  fun getHourStem(dayStem: Stem, hourBranch: Branch): Stem {
    // return Stem.get((dayStem.getIndex() % 5) *2 + hourBranch.getIndex());
    // 以下的解法，不用 expose index
    return when (dayStem) {
      Stem.甲, Stem.己 -> StemBranch.get(Stem.甲, Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem()
      Stem.乙, Stem.庚 -> StemBranch.get(Stem.丙, Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem()
      Stem.丙, Stem.辛 -> StemBranch.get(Stem.戊, Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem()
      Stem.丁, Stem.壬 -> StemBranch.get(Stem.庚, Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem()
      Stem.戊, Stem.癸 -> StemBranch.get(Stem.壬, Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem()
    }
  }
}
