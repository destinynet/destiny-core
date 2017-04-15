/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

public abstract class IHouseYearStemGenderImpl extends IHouseAbstractImpl<Tuple2<Stem, Gender>> {

  protected IHouseYearStemGenderImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_STEM_GENDER;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
    return getBranch(Tuple.tuple(year.getStem() , gender));
  }
}
