/**
 * @author smallufo 
 * Created on 2007/5/28 at 上午 4:22:14
 */ 
package destiny.astrology;

import java.util.Map;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 計算星球南北交點 
 * Swiss Ephemeris 實作是 ApsisWithAzimuthImpl
 */
public interface ApsisWithAzimuthIF extends ApsisIF
{
  public Map<Apsis,PositionWithAzimuth> getPositionsWithAzimuths(Star star , Time gmt , Coordinate coordinate , NodeType nodeType , Location location , double temperature , double pressure);
  public PositionWithAzimuth            getPositionWithAzimuth  (Star star , Apsis apsis , Time gmt , Coordinate coordinate ,  NodeType nodeType , Location location , double temperature , double pressure);
}
