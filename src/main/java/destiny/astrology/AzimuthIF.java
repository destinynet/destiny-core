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
public interface AzimuthIF {

  /** 由黃經 , 黃緯 , 求得地平方位角 */
  Azimuth getAzimuthFromEcliptic(Position eclipticPosition, double gmtJulDay , Location location, double temperature, double pressure);

  /** 承上 , Time 版本 */
  default Azimuth getAzimuthFromEcliptic(Position eclipticPosition, Time gmt, Location location, double temperature, double pressure) {
    return getAzimuthFromEcliptic(eclipticPosition , gmt.getGmtJulDay() , location , temperature , pressure);
  }

  /** 由黃經 , 黃緯 , 求得地平方位角 */
  Azimuth getAzimuthFromEquator(Position equatorPosition, double gmtJulDay, Location location, double temperature, double pressure);

  /** 承上 , Time 版本 */
  default Azimuth getAzimuthFromEquator(Position equatorPosition, Time gmt, Location location, double temperature, double pressure) {
    return getAzimuthFromEquator(equatorPosition, gmt.getGmtJulDay(), location, temperature, pressure);
  }


}
