/**
 * @author smallufo 
 * Created on 2007/5/27 at 上午 2:33:01
 */ 
package destiny.astrology;

import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Map;

/**
 * 計算 南/北交點/近點/遠點 的介面 , 不限定只有月球<br>
 * Swiss Ephemeris 實作是 ApsisImpl , 目前僅支援 Planet , Asteroid
 */
public interface IApsis {
  /**
   * 取得全部 Apsis (近點,遠點,北交,南交) 在某刻 (GMT) 的座標 , 通常 Star 會帶入 Planet.MOON
   */
  Map<Apsis,Position> getPositions(Star star , double gmtJulDay , Coordinate coordinate , NodeType nodeType);

  Position            getPosition(Star star , Apsis apsis , double gmtJulDay , Coordinate coordinate , NodeType nodeType);

  /**
   * 取得某 Apsis 在某刻 (GMT) 的座標
   */
  default Position getPosition(Star star , Apsis apsis , ChronoLocalDateTime gmt , Coordinate coordinate , NodeType nodeType) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getPosition(star , apsis , gmtJulDay  , coordinate , nodeType);
  }

}
