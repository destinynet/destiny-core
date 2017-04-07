/**
 * @author smallufo 
 * Created on 2007/11/26 at 下午 10:34:07
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * 純粹資料結構，存放星體(日月/行星/南北交點/恆星)在黃道帶上的度數 (0~360)
 */
public class PointDegree implements Serializable
{
  private final Point   point;
  private double degree;

  /** 此星體在黃道帶上幾度 */
  public PointDegree(Point p , double deg)
  {
    this.point = p;
    this.degree = Utils.getNormalizeDegree(deg);
  }
  
  /** 此星體在某星座幾度 , deg 必須小於 30 , 否則丟出 RuntimeException */
  public PointDegree(Point s , @NotNull ZodiacSign sign , double deg)
  {
    this.point = s;
    if (deg < 0 || deg >= 30)
      throw new RuntimeException("deg must between 0(inclusive) and 30(exclusive). ");
    this.degree = sign.getDegree() + deg;
  }

  public Point getPoint()
  {
    return point;
  }

  public double getDegree()
  {
    return degree;
  }
  
  /** 取得此度數對於此星座，是幾度 */
  public double getDegreeOf(@NotNull ZodiacSign sign)
  {
    return degree-sign.getDegree();
  }
  
  /** 取得黃道此度數所在的星座 */
  public ZodiacSign getZodiacSign()
  {
    return ZodiacSign.getZodiacSign(degree);
  }
  
  @NotNull
  @Override
  public String toString()
  {
    return point.toString()+"/"+degree;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(degree);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((point == null) ? 0 : point.hashCode());
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final PointDegree other = (PointDegree) obj;
    if (Double.doubleToLongBits(degree) != Double.doubleToLongBits(other.degree))
      return false;
    if (point == null)
    {
      if (other.point != null)
        return false;
    }
    else if (!point.equals(other.point))
      return false;
    return true;
  }
  
  
}
