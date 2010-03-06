/**
 * @author smallufo 
 * Created on 2008/6/19 at 上午 2:19:45
 */ 
package destiny.astrology;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import destiny.astrology.classical.AspectEffectiveClassical;
import destiny.astrology.classical.PointDiameterIF;

/** 古典占星術，列出一張星盤中呈現交角的星體以及角度 的實作 */
public class HoroscopeAspectsCalculatorClassical implements HoroscopeAspectsCalculatorIF , Serializable
{
  private Horoscope horoscope;
  
  private final AspectEffectiveClassical classical;// = new AspectEffectiveClassical();
  
  public HoroscopeAspectsCalculatorClassical(AspectEffectiveClassical classical)
  {
    this.classical = classical;
  }
  
  /** 設定交角容許度的實作，例如 ( PointDiameterAlBiruniImpl , 或是 PointDiameterLillyImpl ) */
  public void setPlanetOrbsImpl(PointDiameterIF planetOrbsImpl)
  {
    //System.out.println(getClass().getName() + " 設定交角容許實作：" + planetOrbsImpl.getTitle());
    classical.setPlanetOrbsImpl(planetOrbsImpl);
  }
  
  /** 取得交角容許度的實作，例如 ( PointDiameterAlBiruniImpl 或是 PointDiameterLillyImpl ) */
  public PointDiameterIF getPlanetOrbsImpl()
  {
    return classical.getPointDiameterImpl();
  }

  @Override
  public void setHoroscope(Horoscope horoscope)
  {
    this.horoscope = horoscope;
  }
  
  @Override
  public Map<Point , Aspect> getPointAspect(Point point, Collection<Point> points)
  {
    if (this.horoscope == null)
      throw new RuntimeException(getClass().getName() + " : horoscope is null ! call setHoroscope(horoscope) first !");

    if (point instanceof Planet)
    {
      Map<Point , Aspect> result = Collections.synchronizedMap(new HashMap<Point , Aspect>());
      double planetDeg = horoscope.getPositionWithAzimuth(point).getLongitude();
      
      for(Point eachPoint : points)
      {
        if (eachPoint instanceof Planet && eachPoint != point) 
        {
          //行星才比對
          double eachPlanetDeg = horoscope.getPositionWithAzimuth(eachPoint).getLongitude();

          for(Aspect eachAspect : Aspect.getAngles(Aspect.Importance.HIGH))
          {
            //只比對 0 , 60 , 90 , 120 , 180 五個度數
            if (classical.isEffective((Planet)point , planetDeg , (Planet)eachPoint , eachPlanetDeg , eachAspect))
            {
              result.put(eachPoint , eachAspect);
            }
          }
        }
      }
      return result;      
    }
    //非行星不計算
    return null;
  }


  @Override
  public String getTitle(Locale locale)
  {
    return "古典占星術 : " + classical.getPointDiameterImpl().getTitle(locale);
  }
  
  @Override
  public String getDescription(Locale locale)
  {
    return "古典占星術實作 : " + classical.getPointDiameterImpl().getDescription(locale);
  }



}
