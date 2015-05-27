/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren.golden;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.YearMonthIF;
import destiny.core.chinese.Branch;

import java.io.Serializable;
import java.util.Locale;

public class MonthBranchCombinedImpl implements MonthBranchIF , Descriptive , Serializable {

  private final YearMonthIF yearMonthImpl;

  public MonthBranchCombinedImpl(YearMonthIF yearMonthImpl) {this.yearMonthImpl = yearMonthImpl;}

  @Override
  public String getTitle(Locale locale) {
    return "月支六合（過節）";
  }

  @Override
  public String getDescription(Locale locale) {
    return "純粹以八字月支六合取月將";
  }

  @Override
  public Branch getBranch(Time lmt, Location location) {
    Branch monthBranch = yearMonthImpl.getMonth(lmt , location).getBranch();
    return monthBranch.getCombined();
  }
}
