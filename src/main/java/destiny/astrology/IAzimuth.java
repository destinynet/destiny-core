/**
 * @author smallufo
 * Created on 2007/5/21 at 上午 5:46:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 計算地平方位角 , 與 Point/Star/Planet/...等星體種類皆無關，只要座標即可
 * 內定實作是 AzimuthImpl
 */
public interface IAzimuth {

  /** 由黃經 , 黃緯 , 求得地平方位角 */
  Azimuth getAzimuthFromEcliptic(Position eclipticPosition, double gmtJulDay , double geoLng , double geoLat , double geoAlt , double temperature, double pressure);

  default Azimuth getAzimuthFromEcliptic(Position eclipticPosition, double gmtJulDay , double geoLng , double geoLat , double geoAlt) {
    return getAzimuthFromEcliptic(eclipticPosition , gmtJulDay , geoLng , geoLat , geoAlt , 0 , 1013.25);
  }

  default Azimuth getAzimuthFromEcliptic(Position eclipticPosition, double gmtJulDay , Location location, double temperature, double pressure) {
    return getAzimuthFromEcliptic(eclipticPosition , gmtJulDay , location.getLongitude() , location.getLatitude() , location.getAltitudeMeter() , temperature , pressure);
  }

  default Azimuth getAzimuthFromEcliptic(Position eclipticPosition, double gmtJulDay , Location location) {
    return getAzimuthFromEcliptic(eclipticPosition , gmtJulDay , location , 0 , 1013.25);
  }

  /** 承上 , ChronoLocalDateTime 版本 */
  default Azimuth getAzimuthFromEcliptic(Position eclipticPosition, ChronoLocalDateTime gmt, Location location, double temperature, double pressure) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getAzimuthFromEcliptic(eclipticPosition , gmtJulDay, location , temperature , pressure);
  }

  /** 由黃經 , 黃緯 , 求得地平方位角 */
  Azimuth getAzimuthFromEquator(Position equatorPosition, double gmtJulDay, double geoLng , double geoLat , double geoAlt , double temperature, double pressure);

  default Azimuth getAzimuthFromEquator(Position equatorPosition, double gmtJulDay, Location location, double temperature, double pressure) {
    return getAzimuthFromEquator(equatorPosition , gmtJulDay , location.getLongitude() , location.getLatitude() , location.getAltitudeMeter() , temperature , pressure);
  }

  /** 承上 , ChronoLocalDateTime 版本 */
  default Azimuth getAzimuthFromEquator(Position equatorPosition, ChronoLocalDateTime gmt, Location location, double temperature, double pressure) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getAzimuthFromEquator(equatorPosition, gmtJulDay, location, temperature, pressure);
  }


}
