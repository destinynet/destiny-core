/*
 * @author smallufo
 * @date 2004/10/29
 * @time 下午 09:57:05
 */
package destiny.astrology;

import destiny.core.calendar.Time;

/**
 * <pre>
 * 計算兩星呈現某交角的時間
 * Swiss ephemeris 的實作是 RelativeTransitImpl
 * </pre> 
 */
public interface RelativeTransitIF
{
  /**
   * <pre>
   * 計算兩星下一個/上一個交角。
   * 注意！angle 有方向性，如果算相刑的角度，別忘了加上 270度
   * TODO : ，目前 RelativeTransitImpl 僅支援 Planet 以及 Asteroid
   * 傳回的 Time 是 GMT
   * </pre>
   */
  public Time getRelativeTransit(Star transitStar , Star relativeStar , double angle , Time startGmtTime , boolean isForward);
}
