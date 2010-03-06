/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 02:56:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 * SwissEph 的實作，是 RiseTransImpl
 */
public interface RiseTransIF
{
  /**
   * 來源、目標時間都是 GMT
   */
  public Time getGmtTransTime(Time fromGmtTime , Star star , TransPoint point , Location location , 
      double atmosphericPressure , double atmosphericTemperature , boolean isDiscCenter , boolean hasRefraction);
}
