/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/** (年支,時支) -> 地支 */
public abstract class IHouseYearBranchHourBranchImpl extends IHouseAbstractImpl<Tuple2<Branch , Branch>> {

  protected IHouseYearBranchHourBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
    return getBranch(Tuple.tuple(year.getBranch() , hour));
  }
}
