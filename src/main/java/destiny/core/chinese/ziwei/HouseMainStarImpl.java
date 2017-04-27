/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple5;

import java.util.Map;

/**
 * 14 顆主星
 * (局數,生日,是否閏月,前一個月幾天)
 */
public abstract class HouseMainStarImpl extends HouseAbstractImpl<Tuple5<Integer, Integer, Boolean, Integer, IPurpleStarBranch>>{

  private final static Map<ZSettings.LeapPurple, IPurpleStarBranch> map = new ImmutableMap.Builder<ZSettings.LeapPurple, IPurpleStarBranch>()
    .put(ZSettings.LeapPurple.LEAP_PURPLE_DEFAULT, new PurpleStarBranchDefaultImpl())
    .put(ZSettings.LeapPurple.LEAP_PURPLE_ACCUM_DAYS, new PurpleStarBranchLeapImpl()) // 如果前一個月，有 30 天，才用此法
    .build();

  protected HouseMainStarImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.MAIN_STAR;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZSettings settings) {
    if (!leap) {
      return getBranch(Tuple.tuple(set , days , leap , prevMonthDays , map.get(ZSettings.LeapPurple.LEAP_PURPLE_DEFAULT)));
    } else {
      // 閏月
      if (days + prevMonthDays == 30) {
        return getBranch(Tuple.tuple(set , 30 , leap , prevMonthDays , map.get(ZSettings.LeapPurple.LEAP_PURPLE_DEFAULT)));
      } else {
        return getBranch(Tuple.tuple(set , days , leap , prevMonthDays , map.get(settings.getLeapPurple())));
      }
    }
//    if (prevMonthDays < 30)
//      return getBranch(Tuple.tuple(set , days , leap , prevMonthDays , map.get(ZSettings.PurpleStar.PURPLE_DEFAULT)));
//    else {
//      return getBranch(Tuple.tuple(set , days , leap , prevMonthDays , map.get(settings.getPurpleStar())));
//    }
  }
}
