/**
 * Created by smallufo on 2017-11-07.
 */
package destiny.astrology;

import java.io.Serializable;

/**
 * 某時某刻某地點，觀測到的日食相關資訊
 */
public class EclipseObservation implements Serializable {

  /** 日全食、環食、或是偏食 */
  private final AbstractEclipse.Type type;

  /** 是否有 centerLine TODO : 不太確定是「當下」亦或是「全程」 , 待查 */
  private final boolean centerLine;

  private final Azimuth azimuth;

  /** 太陽半徑被蓋住的比例 */
  private final double magnitude;

  /** 太陽面積被蓋住的比例 */
  private final double obscuration;

  public EclipseObservation(AbstractEclipse.Type type, boolean centerLine, Azimuth azimuth, double magnitude, double obscuration) {
    this.type = type;
    this.centerLine = centerLine;
    this.azimuth = azimuth;
    this.magnitude = magnitude;
    this.obscuration = obscuration;
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

  public double getMagnitude() {
    return magnitude;
  }

  public double getObscuration() {
    return obscuration;
  }


  @Override
  public String toString() {
    return "[EclipseObservation " + "type=" + type + ", centerLine=" + centerLine + ", azimuth=" + azimuth + ", magnitude=" + magnitude + ", obscuration=" + obscuration + ']';
  }
}
