/**
 * Created by smallufo at 2008/11/11 下午 5:21:34
 */
package destiny.astrology;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;

/**
 * 計算星體在黃道帶上 逆行 / Stationary (停滯) 的介面，目前 SwissEph 的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！)
 * SwissEph 內定實作是 RetrogradeImpl
 */
public interface RetrogradeIF {

  /** 下次停滯的時間為何時 (GMT) */
  double getNextStationary(Star star, double fromGmt, boolean isForward);

  /** 下次停滯的時間為何時 (GMT) */
  default ChronoLocalDateTime<? extends ChronoLocalDate> getNextStationaryDateTime(Star star, ChronoLocalDateTime fromGmt, boolean isForward) {
    double fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt);
    double resultGmt = getNextStationary(star , fromGmtJulDay , isForward);
    return JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(resultGmt);
  }
}
