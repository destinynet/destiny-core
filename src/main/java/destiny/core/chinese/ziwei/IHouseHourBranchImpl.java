/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;

/**
 * 時支 -> 地支
 */
public abstract class IHouseHourBranchImpl implements IHouse<Branch> {

  @Override
  public FuncType getFuncType() {
    return FuncType.HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(Stem yearStem, Branch yearBranch, Branch monthBranch, int monthNum, int days, Branch hour, int set) {
    return getBranch(hour);
  }
}
