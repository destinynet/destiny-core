/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

/** 年支 -> 地支 */
public abstract class HouseYearBranchImpl extends HouseAbstractImpl<Branch> {

  protected HouseYearBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZSettings settings) {
    return getBranch(year.getBranch());
  }
}
