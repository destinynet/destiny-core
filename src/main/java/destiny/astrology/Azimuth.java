/**
 * @author smallufo 
 * Created on 2007/5/21 at 上午 5:46:51
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

/**
 * 地平方位角 , 地平座標系統 (Horizontal Coordinate System) 
 */
public class Azimuth implements java.io.Serializable
{
  /** 地平方位角 , 以北為 0度，東為90度，南為 180度，西為 270度 */
  private double degree; 
  /** 真方位角 */
  private double trueAltitude; 
  /** 視方位角 */
  private double apparentAltitude;
  
  public Azimuth()
  {
  }
  
  public Azimuth(double degree , double trueAltitude , double apparentAltitude)
  {
    this.degree = degree;
    this.trueAltitude = trueAltitude;
    this.apparentAltitude = apparentAltitude;
  }
  
  public double getApparentAltitude()
  {
    return apparentAltitude;
  }
  public void setApparentAltitude(double apparentAltitude)
  {
    this.apparentAltitude = apparentAltitude;
  }
  public double getDegree()
  {
    return degree;
  }
  public void setDegree(double degree)
  {
    this.degree = degree;
  }
  public double getTrueAltitude()
  {
    return trueAltitude;
  }
  public void setTrueAltitude(double trueAltitude)
  {
    this.trueAltitude = trueAltitude;
  }

  @NotNull
  @Override
  public String toString()
  {
    return "Azimuth [degree=" + degree + ", trueAltitude=" + trueAltitude + ", apparentAltitude=" + apparentAltitude + "]";
  } 
}
