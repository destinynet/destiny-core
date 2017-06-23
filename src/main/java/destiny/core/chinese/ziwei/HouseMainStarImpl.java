/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple5;

import java.util.Optional;

/**
 * 14 顆主星
 * (局數,生日,是否閏月,前一個月幾天,當下節氣地支)
 */
public abstract class HouseMainStarImpl extends HouseAbstractImpl<Tuple5<Integer, Integer, Boolean, Integer, IPurpleStarBranch>>{

  private final static IPurpleStarBranch defaultImpl = new PurpleStarBranchDefaultImpl();

  HouseMainStarImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int finalMonthNumForMonthStars, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, Optional<Branch> predefinedMainHouse, ZContext context) {
    if (!leap) {
      return getBranch(Tuple.tuple(set , days , false , prevMonthDays , defaultImpl));
    } else {
      // 閏月
      if (days + prevMonthDays == 30) {
        return getBranch(Tuple.tuple(set , 30 , true , prevMonthDays , defaultImpl));
      } else {
        // 閏月，且「日數＋前一個月的月數」超過 30天，就啟用注入進來的演算法 . 可能會累加日數
        return getBranch(Tuple.tuple(set , days , true , prevMonthDays , context.getPurpleBranchImpl() ));
      }
    }
  }
}
