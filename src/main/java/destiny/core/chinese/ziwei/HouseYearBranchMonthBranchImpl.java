/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.jooq.lambda.tuple.Tuple2;

/**
 * (年支,月支) -> 地支
 * 目前只有 {@link HouseFunctions#house天馬} 使用
 */
public abstract class HouseYearBranchMonthBranchImpl extends HouseAbstractImpl<Tuple2<Branch , Branch>> {

  protected HouseYearBranchMonthBranchImpl(ZStar star) {
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
