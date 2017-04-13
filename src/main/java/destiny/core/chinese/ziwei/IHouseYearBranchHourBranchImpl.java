/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/** (年支,時支) -> 地支 */
public abstract class IHouseYearBranchHourBranchImpl implements IHouse<Tuple2<Branch , Branch>> {

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(Stem yearStem, Branch yearBranch, Branch monthBranch, int monthNum, int days, Branch hour, int set) {
    return getBranch(Tuple.tuple(yearBranch , hour));
  }
}
