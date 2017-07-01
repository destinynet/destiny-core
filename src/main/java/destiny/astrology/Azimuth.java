/**
 * @author smallufo
 * Created on 2007/5/21 at 上午 5:46:51
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 地平方位角 , 地平座標系統 (Horizontal Coordinate System) 
 */
public final class Azimuth implements Serializable {

  /** 地平方位角 , 以北為 0度，東為90度，南為 180度，西為 270度 */
  private final double degree;

  /** 真方位角 */
  private final double trueAltitude;

  /** 視方位角 */
  private final double apparentAltitude;

  public Azimuth(double degree, double trueAltitude, double apparentAltitude) {
    this.degree = degree;
    this.trueAltitude = trueAltitude;
    this.apparentAltitude = apparentAltitude;
  }

  public double getApparentAltitude() {
    return apparentAltitude;
  }

  public double getDegree() {
    return degree;
  }

  public double getTrueAltitude() {
    return trueAltitude;
  }


  @NotNull
  @Override
  public String toString() {
    return "Azimuth [degree=" + degree + ", trueAltitude=" + trueAltitude + ", apparentAltitude=" + apparentAltitude + "]";
  }
}
