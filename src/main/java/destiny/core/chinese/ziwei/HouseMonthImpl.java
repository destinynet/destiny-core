/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

/**
 * 月數 -> 地支
 * 「月數」 : 可能來自節氣、可能來自閏月的計算，總之，會得到一個數字
 */
public abstract class HouseMonthImpl extends HouseAbstractImpl<Integer> {

  HouseMonthImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int finalMonthNumForMonthStars, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    return getBranch(finalMonthNumForMonthStars);
  }
}
