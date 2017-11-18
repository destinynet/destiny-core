/**
 * Created by smallufo on 2017-11-07.
 */
package destiny.astrology.eclipse;

import destiny.astrology.Azimuth;

/**
 * 某時某刻某地點，觀測到的日食相關資訊
 */
public class SolarEclipseObservation extends AbstractEclipseObservation {

  /** 食 的種類 */
  private final AbstractSolarEclipse.SolarType eclipseType;

  /** 直徑被蓋住的比例 */
  private final double magnitude;

  /** 面積被蓋住的比例 */
  private final double obscuration;

  public SolarEclipseObservation(double gmtJulDay, double lng, double lat, double alt, AbstractSolarEclipse.SolarType eclipseType, Azimuth azimuth, double magnitude, double obscuration) {
    super(gmtJulDay , lng , lat , alt , azimuth);

    this.eclipseType = eclipseType;
    this.magnitude = magnitude;
    this.obscuration = obscuration;
  }


  public AbstractSolarEclipse.SolarType getType() {
    return eclipseType;
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
    return "[EclipseObservation "
      + " (lat,lng)=" + lat + "," + lng
      + ", azimuth=" + azimuth
      + ", magnitude=" + magnitude
      + ", obscuration=" + obscuration + ']';
  }
}
