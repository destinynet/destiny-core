/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.chinese;

public class StemBranchUtils {

  /**
   * 五虎遁月
   */
  public final static Stem getMonthStem(Stem yearStem , Branch monthBranch) {
    return Stem.getHeavenlyStems((yearStem.getIndex() % 5) * 2 + (monthBranch.getIndex() >= 2 ? monthBranch.getIndex() : monthBranch.getIndex() + 12));
  }
}
