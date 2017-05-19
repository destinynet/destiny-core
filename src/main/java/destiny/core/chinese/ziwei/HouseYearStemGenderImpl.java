/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/**
 * 年干 + 性別
 * 被用於 博士12神煞
 */
public abstract class HouseYearStemGenderImpl extends HouseAbstractImpl<Tuple2<Stem, Gender>> {

  HouseYearStemGenderImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    return getBranch(Tuple.tuple(lunarYear.getStem() , gender));
  }
}
