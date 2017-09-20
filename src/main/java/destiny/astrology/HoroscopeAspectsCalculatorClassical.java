/**
 * @author smallufo 
 * Created on 2008/6/19 at 上午 2:19:45
 */ 
package destiny.astrology;

import destiny.astrology.classical.AspectEffectiveClassical;
import destiny.astrology.classical.PointDiameterIF;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

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
    //System.out.println(getClass().getOptionalName() + " 設定交角容許實作：" + planetOrbsImpl.getTitle());
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
  
  @NotNull
  @Override
  public Map<Point , Aspect> getPointAspect(Point point, @NotNull Collection<Point> points) {
    if (this.horoscope == null)
      throw new RuntimeException(getClass().getName() + " : horoscope is null ! call setHoroscope(horoscope) first !");

    Map<Point , Aspect> result = Collections.synchronizedMap(new HashMap<>());
    if (point instanceof Planet) {
      double planetDeg = horoscope.getPositionWithAzimuth(point).getLng();

      //行星才比對
      //只比對 0 , 60 , 90 , 120 , 180 五個度數
      points.stream().filter(eachPoint -> eachPoint instanceof Planet && eachPoint != point).forEach(eachPoint -> {
        //行星才比對
        double eachPlanetDeg = horoscope.getPositionWithAzimuth(eachPoint).getLng();

        for (Aspect eachAspect : Aspect.getAngles(Aspect.Importance.HIGH)) {
          //只比對 0 , 60 , 90 , 120 , 180 五個度數
          if (classical.isEffective(point, planetDeg, eachPoint, eachPlanetDeg, eachAspect)) {
            result.put(eachPoint, eachAspect);
          }
        }
      });
    }
    //非行星不計算
    return result;
  }


  @NotNull
  @Override
  public String getTitle(Locale locale)
  {
    return "古典占星術 : " + classical.getPointDiameterImpl().getTitle(locale);
  }
  
  @NotNull
  @Override
  public String getDescription(Locale locale)
  {
    return "古典占星術實作 : " + classical.getPointDiameterImpl().getDescription(locale);
  }



}
