/*
 * @author smallufo
 * @date 2004/11/1
 * @time 下午 10:10:16
 */
package destiny.astrology;

import destiny.core.calendar.Time;

/**
 * 真太陽時計算介面 <br/> 
 * Swiss Ephemeris 實作是 TrueSolarTimeImpl
 */
public interface TrueSolarTimeIF
{
  public double getTrueSolarTimeInSecond(Time gmtTime);
}
