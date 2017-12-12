/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.calendar.chinese;

import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

/**
 * 農曆日期＋時辰的表示法
 */
public class ChineseDateHour extends ChineseDate {

  private final Branch hourBranch;

  public ChineseDateHour(ChineseDate chineseDate , Branch hour) {
    super(chineseDate.getCycleOrZero() , chineseDate.getYear() , chineseDate.getMonth() , chineseDate.isLeapMonth() , chineseDate.getDay());
    this.hourBranch = hour;
  }

  public ChineseDateHour(int cycle, StemBranch year, int month, boolean leapMonth, int day , Branch hour) {
    super(cycle, year, month, leapMonth, day);
    this.hourBranch = hour;
  }

  public Branch getHourBranch() {
    return hourBranch;
  }
}
