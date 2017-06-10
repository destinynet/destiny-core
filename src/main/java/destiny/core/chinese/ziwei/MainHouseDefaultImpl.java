/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;

import java.io.Serializable;

import static destiny.core.chinese.Branch.寅;

/**
 * 命宮 : (月數 , 時支) -> 地支
 * 寅宮開始，順數月數，再逆數時支
 * <p>
 * 假設我的出生月日是農曆 7月6日 巳時，順數生月，那就是從寅宮開始順時針走七步 , 因為是農曆七月，所以經由 順數生月，所以我們找到了申宮
 * <p>
 * 找到申宮之後再 逆數生時 找到了卯宮，所以卯就是你的命宮
 */
@Deprecated
public class MainHouseDefaultImpl implements IMainHouse, Serializable {

  @Override
  public Branch getMainHouse(int finalMonthNum, Branch hour, SolarTerms solarTerms) {
    return 寅.next(finalMonthNum - 1).prev(hour.getIndex());
  }
}
