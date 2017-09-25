/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:14:07
 */
package destiny.astrology;

import destiny.core.calendar.Time;
import destiny.core.calendar.TimeTools;

import java.time.LocalDateTime;

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

  /**
   * 傳回 GMT 時刻
   */
  double getNextTransitGmt(Star star, double degree, Coordinate coordinate , double fromGmt , boolean isForward);

  default LocalDateTime getNextTransitLocalDateTime(Star star, double degree, Coordinate coordinate , double fromGmt , boolean isForward) {
    double gmtJulDay = getNextTransitGmt(star , degree , coordinate , fromGmt , isForward);
    return new Time(gmtJulDay).toLocalDateTime();
  }

  default LocalDateTime getNextTransitGmt(Star star, double degree, Coordinate coordinate , LocalDateTime fromGmt, boolean isForward) {
    double julDay = TimeTools.getGmtJulDay(fromGmt);
    return getNextTransitLocalDateTime(star , degree , coordinate , julDay , isForward);
  }


}
