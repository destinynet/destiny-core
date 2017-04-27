/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

public interface IHouse<T> {

  ZStar getStar();

  FuncType getFuncType();

  Branch getBranch(T t);

  /**
   * @param monthBranch   「節氣」的月令
   * @param monthNum      「單純陰曆」的月令
   * @param leap          是否是閏月
   * @param prevMonthDays [陰曆] 上個月有幾日
   */
  Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZSettings settings);
}
