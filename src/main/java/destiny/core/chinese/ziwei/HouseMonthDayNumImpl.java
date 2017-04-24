/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/**
 * (月數,日數) -> 地支
 */
public abstract class HouseMonthDayNumImpl extends HouseAbstractImpl<Tuple2<Integer, Integer>>{

  protected HouseMonthDayNumImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.MONTH_DAY_NUM;
  }


  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, ZSettings settings) {
    return getBranch(Tuple.tuple(monthNum , days));
  }
}
