/**
 * Created by smallufo on 2017-11-15.
 */
package destiny.astrology.eclipse;

import destiny.astrology.Azimuth;

import java.io.Serializable;

/**
 * 某時某地點，針對某日食、月食的觀測資料
 */
public abstract class AbstractEclipseObservation implements Serializable {

  /** 當下的時間點為何 */
  protected final double gmtJulDay;

  /** 經度 */
  protected final double lng;

  /** 緯度 */
  protected final double lat;

  /** 高度 (米) */
  protected final double alt;

  /** 地平方位角 */
  protected final Azimuth azimuth;


  protected AbstractEclipseObservation(double gmtJulDay, double lng, double lat, double alt, Azimuth azimuth) {
    this.gmtJulDay = gmtJulDay;
    this.lng = lng;
    this.lat = lat;
    this.alt = alt;
    this.azimuth = azimuth;
  }

  public double getGmtJulDay() {
    return gmtJulDay;
  }

  public double getLng() {
    return lng;
  }

  public double getLat() {
    return lat;
  }

  public double getAlt() {
    return alt;
  }

  public Azimuth getAzimuth() {
    return azimuth;
  }
}
