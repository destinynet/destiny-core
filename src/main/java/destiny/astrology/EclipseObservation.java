/**
 * Created by smallufo on 2017-11-07.
 */
package destiny.astrology;

import java.io.Serializable;

/**
 * 某時某刻某地點，觀測到的日食相關資訊
 */
public class EclipseObservation implements Serializable {

  /** 當下的時間點為何 */
  private final double gmtJulDay;

  /** 經度 */
  private final double lng;

  /** 緯度 */
  private final double lat;

  /** 高度 (米) */
  private final double alt;

  /** 日全食、環食、或是偏食 */
  private final AbstractEclipse.Type type;

  /** 是否有 centerLine TODO : 不太確定是「當下」亦或是「全程」 , 待查 */
  private final boolean centerLine;

  /** 太陽的地平方位角 */
  private final Azimuth azimuth;

  /** 半徑被蓋住的比例 */
  private final double magnitude;

  /** 面積被蓋住的比例 */
  private final double obscuration;

  public EclipseObservation(double gmtJulDay, double lng, double lat, double alt, AbstractEclipse.Type type, boolean centerLine, Azimuth azimuth, double magnitude, double obscuration) {
    this.gmtJulDay = gmtJulDay;
    this.lng = lng;
    this.lat = lat;
    this.alt = alt;
    this.type = type;
    this.centerLine = centerLine;
    this.azimuth = azimuth;
    this.magnitude = magnitude;
    this.obscuration = obscuration;
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

  public AbstractEclipse.Type getType() {
    return type;
  }

  public boolean isCenterLine() {
    return centerLine;
  }

  public Azimuth getAzimuth() {
    return azimuth;
  }

  /** 半徑被蓋住的比例 */
  public double getMagnitude() {
    return magnitude;
  }

  /** 面積被蓋住的比例 */
  public double getObscuration() {
    return obscuration;
  }


  @Override
  public String toString() {
    return "[EclipseObservation " + "type=" + type + ", centerLine=" + centerLine + ", azimuth=" + azimuth + ", magnitude=" + magnitude + ", obscuration=" + obscuration + ']';
  }
}
