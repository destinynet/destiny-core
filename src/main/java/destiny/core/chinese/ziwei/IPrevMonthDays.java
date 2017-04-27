/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.chinese.StemBranch;

/**
 * 計算「上個月」的陰曆有幾日
 */
public interface IPrevMonthDays {

  /** 求出上個月陰曆最後一天 */
  ChineseDate getPrevMonthLastDay(int cycle, StemBranch year, int month, boolean leap);

  /** 計算「上個月」的陰曆有幾日 */
  default int getPrevMonthDays(int cycle, StemBranch year, int month, boolean leap) {
    return getPrevMonthLastDay(cycle , year , month , leap).getDay();
  }
}
