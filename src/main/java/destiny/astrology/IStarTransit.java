/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:14:07
 */
package destiny.astrology;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;

import java.time.chrono.ChronoLocalDateTime;
import java.util.function.Function;

/**
 * <pre>
 * 計算某星 Transit 的介面
 * 某星下次（或上次）行進到黃道/恆星 帶上某一點的時間 , 赤道座標系不支援! 
 * SwissEph 內定實作是 StarTransitImpl
 * </pre>
 *
 * TODO : 計算星體 Transit 到黃道某點的時間，僅限於 Planet , Asteroid , Moon's Node
 */
public interface IStarTransit {

  /**
   * 傳回 GMT 時刻
   */
  double getNextTransitGmt(Star star, double degree, Coordinate coordinate , double fromGmt , boolean isForward);


  /**
   * 傳回 GMT
   * */
  default ChronoLocalDateTime getNextTransitGmtDateTime(Star star, double degree, Coordinate coordinate , double fromGmt , boolean isForward , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    double gmtJulDay = getNextTransitGmt(star , degree , coordinate , fromGmt , isForward);
    return revJulDayFunc.apply(gmtJulDay);
  }

  /**
   * 傳回 GMT
   * */
  default ChronoLocalDateTime getNextTransitGmtDateTime(Star star, double degree, Coordinate coordinate , double fromGmt , boolean isForward) {
    return getNextTransitGmtDateTime(star , degree , coordinate , fromGmt , isForward , JulDayResolver1582CutoverImpl::getLocalDateTimeStatic);
  }

}
