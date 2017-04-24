/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

/**
 * (月數 or 節氣月支) -> 地支
 */
public abstract class HouseMonthImpl extends HouseAbstractImpl<Tuple3<ZSettings.MonthType , Integer , Branch>> {

  protected HouseMonthImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.MONTH;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, ZSettings settings) {
    return getBranch(Tuple.tuple(settings.getMonthType() , monthNum , monthBranch));
  }
}
