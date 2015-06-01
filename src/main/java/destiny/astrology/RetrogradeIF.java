/**
 * Created by smallufo at 2008/11/11 下午 5:21:34
 */
package destiny.astrology;

import destiny.core.calendar.Time;

/**
 * 計算星體在黃道帶上 逆行 / Stationary (停滯) 的介面，目前 SwissEph 的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！)
 * SwissEph 內定實作是 RetrogradeImpl
 */
public interface RetrogradeIF
{
  /** 下次停滯的時間為何時 (GMT) */
  Time getNextStationary(Star star , Time fromGmtTime , boolean isForward);
}
