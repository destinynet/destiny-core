/**
 * @author smallufo 
 * Created on 2007/11/22 at 下午 10:42:18
 */ 
package destiny.astrology.classical;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import destiny.astrology.Aspect;
import destiny.astrology.AspectEffectiveIF;
import destiny.astrology.Horoscope;
import destiny.astrology.Point;

/**
 * <pre>
 * 「古典占星」的交角判定，古典占星只計算「星體光芒的容許度」與「交角」是否有效，並沒有考慮交角的類型。
 * （「現代占星」則是各種交角都有不同的容許度）
 * 演算法採用 Templete Method design pattern 
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 未來可以繼承此 Abstract Class , 呼叫資料庫 , 取得個人化的 OrbsMap
 * </pre>
 */
public class AspectEffectiveClassical implements AspectEffectiveIF , Serializable
{
  /** 星芒交角 , 內定採用 PointDiameterAlBiruniImpl , 尚可選擇注入 PointDiameterLillyImpl */
  private PointDiameterIF pointDiameterImpl;// = new PointDiameterAlBiruniImpl();
  
  protected AspectEffectiveClassical()
  {
  }
  
  public AspectEffectiveClassical(PointDiameterIF planetOrbsImpl)
  {
    this.pointDiameterImpl = planetOrbsImpl;
  }
  
  public void setPlanetOrbsImpl(PointDiameterIF impl)
  {
    this.pointDiameterImpl = impl;
  }

  public PointDiameterIF getPointDiameterImpl()
  {
    return pointDiameterImpl;
  }
  
  /**
   * @param p1 Point 1
   * @param deg1 Point 1 於黃道上的度數
   * @param p2 Point 2
   * @param deg2 Point 2 於黃道上的度數
   * @param angles 判定的角度，例如 [0,60,90,120,180]
   * @return 兩顆星是否形成有效交角
   */
  public boolean isEffective(Point p1 , double deg1, Point p2 , double deg2 , double[] angles)
  {
    for(double eachAngle : angles)
    {
      if (Math.abs(Horoscope.getAngle(deg1 , deg2) - eachAngle) <= (pointDiameterImpl.getDiameter(p1) + pointDiameterImpl.getDiameter(p2) ) / 2 )
      {
        //System.out.println("eachAngle = " + eachAngle + " : " + Math.abs(Horoscope.getAngle(deg1 , deg2) - eachAngle) + " < " + ((pointDiameterImpl.getPlanetOrb(p1) + pointDiameterImpl.getPlanetOrb(p2) ) / 2) );
        return true;
      }
    }
    return false;
  }
  
  /** 兩星體是否形成有效交角 */
  @Override
  public boolean isEffective(Point p1 , double deg1 , Point p2 , double deg2 , Aspect aspect)
  {
    return isEffective(p1, deg1, p2, deg2, new double[] {aspect.getDegree()});
  }
  
  /** 兩星體是否形成某些交角 */
  @Override
  public boolean isEffective(Point p1 , double deg1 , Point p2 , double deg2 , Aspect... aspects)
  {
    double[] angles = new double[aspects.length];
    for(int i=0 ; i < angles.length ; i++)
      angles[i] = aspects[i].getDegree();
    return isEffective(p1, deg1, p2, deg2, angles);
  }

  /** 兩星體是否形成某些交角 */
  @Override
  public boolean isEffective(Point p1 , double deg1 , Point p2 , double deg2 , Collection<Aspect> aspects)
  {
    double[] angles = new double[aspects.size()];
    Iterator<Aspect> it = aspects.iterator();
    int i=0;
    while(it.hasNext())
    {
      angles[i] = it.next().getDegree();
      i++;
    }
    return isEffective(p1, deg1, p2, deg2, angles);
  }
}
