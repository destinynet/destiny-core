/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import static destiny.core.chinese.ziwei.FuncType.DAY_NUM_HOUR_BRANCH;

/**
 * (日數,時支) -> 地支
 */
public abstract class HouseDayNumHourBranchImpl extends HouseAbstractImpl<Tuple2<Integer , Branch>> {

  protected HouseDayNumHourBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return DAY_NUM_HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, ZSettings settings) {
    return getBranch(Tuple.tuple(days , hour));
  }
}
