/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

/** 月數 (1-12) -> 地支 */
public abstract class HouseMonthNumImpl extends HouseAbstractImpl<Integer> {

  protected HouseMonthNumImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.MONTH_NUM;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, Settings settings) {
    return getBranch(monthNum);
  }
}
