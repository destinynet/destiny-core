/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;

import java.util.Optional;

/**
 * 年干 系星
 * 必須判斷 {@link ZContext.YearType}
 */
public abstract class HouseYearStemImpl extends HouseAbstractImpl<Stem> {

  HouseYearStemImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int finalMonthNumForMonthStars, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, Optional<Branch> predefinedMainHouse, ZContext context) {
    Stem yearStem = context.getYearType() == ZContext.YearType.YEAR_LUNAR ? lunarYear.getStem() : solarYear.getStem();
    return getBranch(yearStem);
  }
}
