/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.chinese;

public class StemBranchUtils {

  /**
   * 五虎遁年起月(干)
   */
  public static Stem getMonthStem(Stem yearStem , Branch monthBranch) {
    return Stem.get((yearStem.getIndex() % 5) * 2 + (monthBranch.getIndex() >= 2 ? monthBranch.getIndex() : monthBranch.getIndex() + 12));
  }

  /**
   * 五鼠遁日起時(干)
   */
  public static Stem getHourStem(Stem dayStem , Branch hourBranch) {
    // return Stem.getHeavenlyStems((dayStem.getIndex() % 5) *2 + hourBranch.getIndex());
    // 以下的解法，不用 expose index
    switch (dayStem) {
      case 甲:
      case 己:
        return StemBranch.get(Stem.甲 , Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem();
      case 乙:
      case 庚:
        return StemBranch.get(Stem.丙 , Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem();
      case 丙:
      case 辛:
        return StemBranch.get(Stem.戊 , Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem();
      case 丁:
      case 壬:
        return StemBranch.get(Stem.庚 , Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem();
      case 戊:
      case 癸:
        return StemBranch.get(Stem.壬 , Branch.子).next(hourBranch.getAheadOf(Branch.子)).getStem();
      default: throw new AssertionError("error");
    }
  }
}
