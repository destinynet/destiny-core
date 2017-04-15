/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

/** 年支 -> 地支 */
public abstract class IHouseYearBranchImpl extends IHouseAbstractImpl<Branch> {

  protected IHouseYearBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
    return getBranch(year.getBranch());
  }
}
