/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

/**
 * 年干支 ，用於旬空兩顆星
 * 必須判斷 {@link ZContext.YearType}
 */
public abstract class HouseYearImpl extends HouseAbstractImpl<StemBranch> {

  HouseYearImpl(ZStar star) {
    super(star);
  }


  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int finalMonthNumForMonthStars, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    StemBranch year = context.getYearType() == ZContext.YearType.YEAR_LUNAR ? lunarYear : solarYear;

    return getBranch(year);
  }
}
