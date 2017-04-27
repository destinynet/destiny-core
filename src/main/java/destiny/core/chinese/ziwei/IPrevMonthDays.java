/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.StemBranch;

/**
 * 計算「上個月」的陰曆有幾日
 */
public interface IPrevMonthDays {

  int getPrevMonthDays(int cycle, StemBranch year, int month, boolean leap);
}
