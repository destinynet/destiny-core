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
import org.jooq.lambda.tuple.Tuple3;

import static destiny.core.chinese.ziwei.FuncType.FIVE_GENDER_YINYANG;

/** 長生 12 神煞 */
public abstract class HouseFiveGenderYinYangImpl extends HouseAbstractImpl<Tuple3<FiveElement , Gender, YinYangIF>> {

  protected HouseFiveGenderYinYangImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FIVE_GENDER_YINYANG;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZSettings settings) {
    IMainHouse impl = getMainHouseImpl(settings.getMainHouse());
    StemBranch 命宮 = IZiwei.getMainHouse(year.getStem() , monthNum , hour , solarTerms , impl);
    Tuple3<String , FiveElement , Integer> t3 = IZiwei.getNaYin(命宮);
    FiveElement fiveElement = t3.v2();
    return getBranch(Tuple.tuple(fiveElement , gender , year.getStem()));
  }

  private IMainHouse getMainHouseImpl(ZSettings.MainHouse mainHouse) {
    switch (mainHouse) {
      case MAIN_HOUSE_DEFAULT: return new MainHouseDefaultImpl();
      case MAIN_HOUSE_SOLAR: return new MainHouseSolarTermsImpl();
      default: throw new AssertionError("Error : " + mainHouse);
    }
  }
}
