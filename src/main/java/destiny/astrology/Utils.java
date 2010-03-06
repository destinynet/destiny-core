/**
 * @author smallufo 
 * Created on 2007/12/23 at 上午 4:26:17
 */ 
package destiny.astrology;

public class Utils
{

  /** 將度數 normalize 到 0(含)~360(不含) 的區間 */
  public static double getNormalizeDegree(double degree)
  {
    if (degree >= 360)
      return degree % 360;
    else if (degree < 0)
      return   (360-(0-degree) % 360) % 360;
    else
      return degree;
  }

  
}
