/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/**
 * (月支,日數) -> 地支
 */
public abstract class IHouseMonthBranchDayNumImpl extends IHouseAbstractImpl<Tuple2<Branch, Integer>> {

  protected IHouseMonthBranchDayNumImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.MONTH_BRANCH_DAY_NUM;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
    return getBranch(Tuple.tuple(monthBranch , days));
  }
}
