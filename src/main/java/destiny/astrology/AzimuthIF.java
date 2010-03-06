/**
 * @author smallufo 
 * Created on 2007/5/21 at 上午 5:46:16
 */ 
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 計算地平方位角 , 與 Point/Star/Planet/...等星體種類皆無關，只要座標即可
 * 內定實作是 AzimuthImpl
 */
public interface AzimuthIF
{
  /** 由黃經 , 黃緯 , 求得地平方位角 */
  public Azimuth getAzimuthFromEcliptic(Position eclipticPosition , Time gmt , Location location , double temperature , double pressure);
  /** 由赤經 , 赤緯 , 求得地平方位角 */
  public Azimuth getAzimuthFromEquator (Position equatorPosition  , Time gmt , Location location , double temperature , double pressure);
}
