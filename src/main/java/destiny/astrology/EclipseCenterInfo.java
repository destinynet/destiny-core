/**
 * Created by smallufo on 2017-11-07.
 */
package destiny.astrology;

import java.io.Serializable;

/**
 * Eclipse 畫過 Center Line , 某時刻 (Instant) 的資訊
 */
public class EclipseCenterInfo implements Serializable {

  private final double gmtJulDay;

  private final double lng;

  private final double lat;

  private final AbstractEclipse.Type type;

  private final Azimuth azimuth;


  public EclipseCenterInfo(double gmtJulDay, double lng, double lat, AbstractEclipse.Type type, Azimuth azimuth) {
    this.gmtJulDay = gmtJulDay;
    this.lng = lng;
    this.lat = lat;
    this.type = type;
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

  public AbstractEclipse.Type getType() {
    return type;
  }

  public Azimuth getAzimuth() {
    return azimuth;
  }

  @Override
  public String toString() {
    return "[EclipseInfo " + "gmtJulDay=" + gmtJulDay + ", lng=" + lng + ", lat=" + lat + ", type=" + type + ", azimuth=" + azimuth + ']';
  }
}
