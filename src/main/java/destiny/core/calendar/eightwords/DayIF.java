/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 05:10:36
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.StemBranch;

import java.time.LocalDateTime;

/** 取得日干支的介面 */
public interface DayIF {

  /**
   * @param gmtJulDay GMT時間
   * @param location 當地地點
   * @param midnightImpl 實作「子正」的 class
   * @param hourImpl 實作時辰劃分的 class
   * @param changeDayAfterZi 子時過後是否換日
   * @return 日辰干支
   */
  StemBranch getDay(double gmtJulDay, Location location , MidnightIF midnightImpl , HourIF hourImpl , boolean changeDayAfterZi);


  StemBranch getDay(LocalDateTime lmt, Location location , MidnightIF midnightImpl , HourIF hourImpl , boolean changeDayAfterZi);

  /**
   * @param lmt 當地時間
   * @param location 當地地點
   * @param midnightImpl 實作「子正」的 class
   * @param hourImpl 實作時辰劃分的 class
   * @param changeDayAfterZi 子時過後是否換日
   * @return 日辰干支
   */
  default StemBranch getDay(Time lmt , Location location , MidnightIF midnightImpl , HourIF hourImpl , boolean changeDayAfterZi) {
    return getDay(lmt.toLocalDateTime() , location , midnightImpl , hourImpl , changeDayAfterZi);
  }
}
