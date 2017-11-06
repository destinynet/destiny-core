/**
 * Created by smallufo on 2017-11-07.
 */
package destiny.astrology;

import java.io.Serializable;

/**
 * Eclipse 的 Center Line 資料
 */
public class EclipseInfo implements Serializable {

  private final double longitude;

  private final double latitude;

  private final AbstractEclipse.Type type;

  private final Azimuth azimuth;


  public EclipseInfo(double longitude, double latitude, AbstractEclipse.Type type, Azimuth azimuth) {
    this.longitude = longitude;
    this.latitude = latitude;
    this.type = type;
    this.azimuth = azimuth;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public AbstractEclipse.Type getType() {
    return type;
  }

  public Azimuth getAzimuth() {
    return azimuth;
  }


  @Override
  public String toString() {
    return "[EclipseLocation " + "longitude=" + longitude + ", latitude=" + latitude + ", type=" + type + ", azimuth=" + azimuth + ']';
  }
}
