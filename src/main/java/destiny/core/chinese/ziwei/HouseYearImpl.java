/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

/**
 * 年干支 ，用於旬空兩顆星
 * 參數為 年的型態(lunar / solar) , 陰曆年干支、節氣年干支
 */
public abstract class HouseYearImpl extends HouseAbstractImpl<Tuple3<ZContext.YearType , StemBranch , StemBranch>> {

  protected HouseYearImpl(ZStar star) {
    super(star);
  }


  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    //return getBranch(lunarYear);
    return getBranch(Tuple.tuple(context.getYearType() , lunarYear , solarYear));
  }
}
