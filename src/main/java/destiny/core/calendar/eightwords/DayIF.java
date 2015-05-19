/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 05:10:36
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.StemBranch;

/** 取得日干支的介面 */
public interface DayIF {
  /**
   * @param lmt 當地時間
   * @param location 當地地點
   * @param midnightImpl 實作「子正」的 class
   * @param hourImpl 實作時辰劃分的 class
   * @param changeDayAfterZi 子時過後是否換日
   * @return 日辰干支
   */
  StemBranch getDay(Time lmt , Location location , MidnightIF midnightImpl , HourIF hourImpl , boolean changeDayAfterZi);
}
