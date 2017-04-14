/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

/** (年支、月數、時支) -> 地支 */
public abstract class IHouseYearBranchMonthNumHourBranchImpl extends IHouseAbstractImpl<Tuple3<Branch, Integer , Branch >> {

  protected IHouseYearBranchMonthNumHourBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_MONTH_NUM_HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Settings settings) {
    return getBranch(Tuple.tuple(year.getBranch() , monthNum , hour));
  }
}
