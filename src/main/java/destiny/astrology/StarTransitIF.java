/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:14:07
 */
package destiny.astrology;

import destiny.core.calendar.Time;

/**
 * <pre>
 * 計算某星 Transit 的介面
 * 某星下次（或上次）行進到黃道/恆星 帶上某一點的時間 , 赤道座標系不支援! 
 * SwissEph 內定實作是 StarTransitImpl
 * </pre>
 */
public interface StarTransitIF
{
  //TODO : 計算星體 Transit 到黃道某點的時間，僅限於 Planet , Asteroid , Moon's Node
  Time getNextTransit(Star star, double degree, Coordinate coordinate , double fromGmt , boolean isForward);

  default Time getNextTransit(Star star, double degree, Coordinate coordinate , Time fromGmtTime , boolean isForward) {
    return getNextTransit(star , degree , coordinate , fromGmtTime.getGmtJulDay() , isForward);
  }
}
