/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;

/** 年干 -> 地支 */
public abstract class IHouseYearStemImpl implements IHouse<Stem> {

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_STEM;
  }

  @Override
  public Branch getBranch(Stem yearStem, Branch yearBranch, Branch monthBranch, int monthNum, int days, Branch hour, int set) {
    return getBranch(yearStem);
  }
}
