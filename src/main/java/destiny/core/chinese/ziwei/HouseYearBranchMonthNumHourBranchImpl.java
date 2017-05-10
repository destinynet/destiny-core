/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

/** (年支、月數、時支) -> 地支 */
public abstract class HouseYearBranchMonthNumHourBranchImpl extends HouseAbstractImpl<Tuple3<Branch, Integer , Branch >> {

  protected HouseYearBranchMonthNumHourBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_MONTH_NUM_HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch yinYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    return getBranch(Tuple.tuple(yinYear.getBranch() , monthNum , hour));
  }
}
