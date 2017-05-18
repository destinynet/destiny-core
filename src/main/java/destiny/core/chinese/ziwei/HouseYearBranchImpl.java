/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

/**
 * 年支 -> 地支
 * 其中的「年支」，可能是陰曆、也可能是節氣
 * 鍾義明 的書籍特別提出，年系星，應該用立春分界 , 參考截圖 http://imgur.com/WVUxCc8
 * Tuple3<類型 , 陰曆 , 節氣>
 */
public abstract class HouseYearBranchImpl extends HouseAbstractImpl<Tuple3<ZContext.YearType, Branch , Branch>> {

  protected HouseYearBranchImpl(ZStar star) {
    super(star);
  }


  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    return getBranch(Tuple.tuple(context.getYearType() , lunarYear.getBranch(), solarYear.getBranch()));
  }
}
