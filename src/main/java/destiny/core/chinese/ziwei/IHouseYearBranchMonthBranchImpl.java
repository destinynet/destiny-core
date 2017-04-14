/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple2;

import static destiny.core.chinese.ziwei.LuckyStar.fun天馬;

public abstract class IHouseYearBranchMonthBranchImpl extends IHouseAbstractImpl<Tuple2<Branch , Branch>> {

  protected IHouseYearBranchMonthBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_MONTH_BRANCH;
  }

  @Override
  public Branch getBranch(Tuple2<Branch, Branch> objects) {
    throw new RuntimeException("error : " + objects);
  }

}
