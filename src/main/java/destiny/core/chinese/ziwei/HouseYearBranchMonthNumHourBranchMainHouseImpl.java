/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple5;

/**
 * 只有 {@link StarMinor#天才} 在用 : {@link StarMinor#fun天才}
 */
public abstract class HouseYearBranchMonthNumHourBranchMainHouseImpl extends
  HouseAbstractImpl<Tuple5<Branch, Integer , Branch , SolarTerms , IMainHouse>> {

  protected HouseYearBranchMonthNumHourBranchMainHouseImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_MONTH_NUM_HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {

    IMainHouse mainHouseImpl = context.getMainHouseImpl();

    return getBranch(Tuple.tuple(year.getBranch(), monthNum , hour , solarTerms , mainHouseImpl));
  }
}
