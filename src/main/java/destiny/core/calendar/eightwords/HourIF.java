/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:49:08
 */
package destiny.core.calendar.eightwords;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.EarthlyBranches;
import org.jetbrains.annotations.NotNull;

/** 時辰的分界點實作 , SwissEph 的實作是 HourSolarTransImpl */
public interface HourIF extends Descriptive
{
  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @return 時辰（只有地支）
   */
  @NotNull
  public EarthlyBranches getHour(Time lmt , Location location);
  
  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @param eb 欲求之下一個地支開始時刻
   * @return 下一個時辰開始的時刻
   */
  @NotNull
  public Time getLmtNextStartOf(Time lmt , Location location , EarthlyBranches eb);
  
}
