/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

/** 月支 -> 地支 */
public abstract class IHouseMonthBranchImpl implements IHouse<Branch> {

  @Override
  public FuncType getFuncType() {
    return FuncType.MONTH_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Settings settings) {
    return getBranch(monthBranch);
  }
}
