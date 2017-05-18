/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

/**
 * 年干 系星
 * Tuple<類型 , 陰曆年干 , 節氣年干>
 */
public abstract class HouseYearStemImpl extends HouseAbstractImpl<Tuple3<ZContext.YearType , Stem, Stem>> {

  protected HouseYearStemImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    return getBranch(Tuple.tuple(context.getYearType() , lunarYear.getStem() , solarYear.getStem()));
  }
}
