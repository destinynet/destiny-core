/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.YinYangIF;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

/** 長生 12 神煞 */
public abstract class HouseFiveGenderYinYangImpl extends HouseAbstractImpl<Tuple3<FiveElement , Gender, YinYangIF>> {

  protected HouseFiveGenderYinYangImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch yinYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    IMainHouse impl = context.getMainHouseImpl();
    StemBranch 命宮 = IZiwei.getMainHouse(yinYear.getStem() , monthNum , hour , solarTerms , impl);
    Tuple2<FiveElement , Integer> t3 = IZiwei.getMainDesc(命宮);
    FiveElement fiveElement = t3.v1();
    return getBranch(Tuple.tuple(fiveElement , gender , yinYear.getStem()));
  }

}
