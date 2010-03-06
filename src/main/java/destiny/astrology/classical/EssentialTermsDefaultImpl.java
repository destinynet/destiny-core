/**
 * @author smallufo 
 * Created on 2007/11/28 at 下午 3:06:17
 */ 
package destiny.astrology.classical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.PointDegree;
import destiny.astrology.Utils;
import destiny.astrology.ZodiacSign;

/**
 * Essential Terms , 內定實作 , 參考 Ptolemy's Table , 以五分法
 */
public class EssentialTermsDefaultImpl implements EssentialTermsIF , Serializable
{
  private static List<PointDegree> degList = new ArrayList<PointDegree>();
  static
  {
    //戌
    degList.add(new PointDegree(Planet.JUPITER , 6));
    degList.add(new PointDegree(Planet.VENUS , 14));
    degList.add(new PointDegree(Planet.MERCURY , 21));
    degList.add(new PointDegree(Planet.MARS , 26));
    degList.add(new PointDegree(Planet.SATURN , 30));
    //酉
    degList.add(new PointDegree(Planet.VENUS , 38));
    degList.add(new PointDegree(Planet.MERCURY , 45));
    degList.add(new PointDegree(Planet.JUPITER , 52));
    degList.add(new PointDegree(Planet.SATURN , 56));
    degList.add(new PointDegree(Planet.MARS , 60));
    //申
    degList.add(new PointDegree(Planet.MERCURY , 67));
    degList.add(new PointDegree(Planet.JUPITER , 74));
    degList.add(new PointDegree(Planet.VENUS , 81));
    degList.add(new PointDegree(Planet.SATURN , 85));
    degList.add(new PointDegree(Planet.MARS , 90));
    //未
    degList.add(new PointDegree(Planet.MARS , 96));
    degList.add(new PointDegree(Planet.JUPITER , 103));
    degList.add(new PointDegree(Planet.MERCURY , 110));
    degList.add(new PointDegree(Planet.VENUS , 117));
    degList.add(new PointDegree(Planet.SATURN , 120));
    //午
    degList.add(new PointDegree(Planet.SATURN , 126));
    degList.add(new PointDegree(Planet.MERCURY , 133));
    degList.add(new PointDegree(Planet.VENUS , 139));
    degList.add(new PointDegree(Planet.JUPITER , 145));
    degList.add(new PointDegree(Planet.MARS , 150));
    //巳
    degList.add(new PointDegree(Planet.MERCURY , 157));
    degList.add(new PointDegree(Planet.VENUS , 163));
    degList.add(new PointDegree(Planet.JUPITER , 168));
    degList.add(new PointDegree(Planet.SATURN , 174));
    degList.add(new PointDegree(Planet.MARS , 180));
    //辰
    degList.add(new PointDegree(Planet.SATURN , 186));
    degList.add(new PointDegree(Planet.VENUS , 191));
    degList.add(new PointDegree(Planet.JUPITER , 199));
    degList.add(new PointDegree(Planet.MERCURY , 204));
    degList.add(new PointDegree(Planet.MARS , 210));
    //卯
    degList.add(new PointDegree(Planet.MARS , 216));
    degList.add(new PointDegree(Planet.JUPITER , 224));
    degList.add(new PointDegree(Planet.VENUS , 231));
    degList.add(new PointDegree(Planet.MERCURY , 237));
    degList.add(new PointDegree(Planet.SATURN , 240));
    //寅
    degList.add(new PointDegree(Planet.JUPITER , 248));
    degList.add(new PointDegree(Planet.VENUS , 254));
    degList.add(new PointDegree(Planet.MERCURY , 259));
    degList.add(new PointDegree(Planet.SATURN , 265));
    degList.add(new PointDegree(Planet.MARS , 270));
    //丑
    degList.add(new PointDegree(Planet.VENUS , 276));
    degList.add(new PointDegree(Planet.MERCURY , 282));
    degList.add(new PointDegree(Planet.JUPITER , 289));
    degList.add(new PointDegree(Planet.MARS , 295));
    degList.add(new PointDegree(Planet.SATURN , 300));
    //子
    degList.add(new PointDegree(Planet.SATURN , 306));
    degList.add(new PointDegree(Planet.MERCURY , 312));
    degList.add(new PointDegree(Planet.VENUS , 320));
    degList.add(new PointDegree(Planet.JUPITER , 325));
    degList.add(new PointDegree(Planet.MARS , 330));
    //亥
    degList.add(new PointDegree(Planet.VENUS , 338));
    degList.add(new PointDegree(Planet.JUPITER , 344));
    degList.add(new PointDegree(Planet.MERCURY , 350));
    degList.add(new PointDegree(Planet.MARS , 356));
    degList.add(new PointDegree(Planet.SATURN , 359.999999999999)); //如果改成 360 , 會被 normalize 成 0 度
  }

  @Override
  public Point getTermsStar(double degree)
  {
    double normalizedDegree = Utils.getNormalizeDegree(degree);
    int signIndex = (int) normalizedDegree / 30;
    for (int i=0 ; i<5 ; i++)
    {
      PointDegree pointDegree = degList.get(signIndex*5+i);
      if (normalizedDegree < pointDegree.getDegree())
        return pointDegree.getPoint();
    }
    throw new RuntimeException("Cannot find Essential Terms at degree " + degree + " , signIndex = " + signIndex);
  }

  @Override
  public Point getTermsStar(ZodiacSign sign, double degree)
  {
    return getTermsStar(sign.getDegree() + degree);
  }
}

