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

/** (年支、月數、時支) -> 地支
 *
 * 只有  {@link StarMinor#天壽}
 * */
public abstract class HouseYearBranchMonthNumHourBranchImpl extends HouseAbstractImpl<Tuple3<Branch, Integer , Branch >> {

  HouseYearBranchMonthNumHourBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    Branch yearBranch = context.getYearType() == ZContext.YearType.YEAR_LUNAR ? lunarYear.getBranch() : solarYear.getBranch();
    return getBranch(Tuple.tuple(yearBranch , monthNum , hour));
  }
}
