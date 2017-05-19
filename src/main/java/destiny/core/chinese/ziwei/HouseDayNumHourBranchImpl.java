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

/**
 * (日數,時支) -> 地支
 */
public abstract class HouseDayNumHourBranchImpl extends HouseAbstractImpl<Tuple2<Integer , Branch>> {

  HouseDayNumHourBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    return getBranch(Tuple.tuple(days , hour));
  }
}
