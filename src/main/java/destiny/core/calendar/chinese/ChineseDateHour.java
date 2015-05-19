/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.calendar.chinese;

import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.StemBranch;

/**
 * 農曆日期＋時辰的表示法
 */
public class ChineseDateHour extends ChineseDate {

  private final EarthlyBranches hourBranch;

  public ChineseDateHour(ChineseDate chineseDate , EarthlyBranches hour) {
    super(chineseDate.getCycle() , chineseDate.getYear() , chineseDate.getMonth() , chineseDate.isLeapMonth() , chineseDate.getDay());
    this.hourBranch = hour;
  }

  public ChineseDateHour(int cycle, StemBranch year, int month, boolean leapMonth, int day , EarthlyBranches hour) {
    super(cycle, year, month, leapMonth, day);
    this.hourBranch = hour;
  }

  public EarthlyBranches getHourBranch() {
    return hourBranch;
  }
}
