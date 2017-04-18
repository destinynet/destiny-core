/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;

import java.io.Serializable;

import static destiny.core.chinese.Branch.寅;

/**
 * 與「節氣」的「節」相關
 *
 * 假如你於今年二月初一日生了一個孩子，用斗數起盤，兩派的演算法便有不同。
 * 依月支來起的，因今年二月初三才交「驚蟄」，要交「驚蟄」後才算卯月二月，所以便照寅月（正月）來推算，排出的星盤便完全與前者不同。
 */
public class MainHouseSolarTermsImpl implements IMainHouse , Serializable {

  @Override
  public Branch getMainHouse(int month, Branch hour, SolarTerms solarTerms) {
    int indexOf節 = solarTerms.ordinal() / 2 +1;
    return 寅.next(indexOf節 - 1).prev(hour.getIndex());
  }
}
