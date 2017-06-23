/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

import java.util.Optional;

public interface IHouse<T> {

  ZStar getStar();

  Branch getBranch(T t);

  /**
   * @param lunarYear                  「陰曆」的年干支
   * @param solarYear                  「節氣」的年干支
   * @param monthBranch                「節氣」的月令
   * @param finalMonthNumForMonthStars 最終依據的月令數字
   * @param leap                       是否是閏月
   * @param prevMonthDays              [陰曆] 上個月有幾日
   * @param predefinedMainHouse        預先計算好的「命宮」
   */
  Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int finalMonthNumForMonthStars,
                   SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays,
                   Optional<Branch> predefinedMainHouse, ZContext context);


}
