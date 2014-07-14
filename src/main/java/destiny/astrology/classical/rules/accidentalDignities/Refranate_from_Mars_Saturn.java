/**
 * Created by smallufo at 2008/11/11 下午 8:29:36
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.beans.RefranationBean;
import destiny.astrology.classical.RefranationIF;
import destiny.utils.Triple;
import destiny.utils.Tuple;

/**
 * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
 */
public final class Refranate_from_Mars_Saturn extends Rule
{
  private RefranationIF refranationImpl;

  public Refranate_from_Mars_Saturn(RefranationIF refranationImpl) {
    this.refranationImpl = refranationImpl;
  }

//  /** 判斷入相位，或是出相位 的實作 , 內定採用古典占星 */
//  private AspectApplySeparateIF aspectApplySeparateImpl;// = new AspectApplySeparateImpl(new AspectEffectiveClassical());
//
//  /** 計算兩星呈現某交角的時間 , 內定是 Swiss ephemeris 的實作 */
//  private final RelativeTransitIF relativeTransitImpl;// = new RelativeTransitImpl();
//
//  /** 計算星體逆行的介面，目前只支援 Planet , 目前的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！) */
//  private final RetrogradeIF retrogradeImpl;// = new RetrogradeImpl();
//
//  public Refranate_from_Mars_Saturn(AspectApplySeparateIF aspectApplySeparateImpl , RelativeTransitIF relativeTransitImpl , RetrogradeIF retrogradeImpl , AspectEffectiveClassical aspectEffectiveClassical)
//  {
//    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
//    this.relativeTransitImpl = relativeTransitImpl;
//    this.retrogradeImpl = retrogradeImpl;
//    this.aspectApplySeparateImpl = new AspectApplySeparateImpl(aspectEffectiveClassical);
//  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
  //太陽 / 月亮不會逆行
    if (planet == Planet.MOON || planet == Planet.SUN)
      return null;
    
    Point otherPoint;
    RefranationBean bean;
    
    
    if (planet != Planet.MARS)
    {
      otherPoint = Planet.MARS;
      Triple<Boolean , Point, Aspect> t = refranationImpl.resultOf(horoscopeContext, planet, otherPoint);
      //bean = new RefranationBean(horoscopeContext , planet , otherPoint , aspectApplySeparateImpl , relativeTransitImpl , retrogradeImpl);
      if (t.getFirst() == true)
      {
        //addComment(Locale.TAIWAN, planet + " 逃過了與 " + otherPoint + " 形成 " + bean.getApplyingAspect() + " (Refranation)");
        //return new Tuple<String , Object[]>("comment" , new Object[] {planet , otherPoint , bean.getApplyingAspect()} );
        return new Tuple<String , Object[]>("comment" , new Object[] {planet , otherPoint , t.getThird()} );
      }
    }
    
    if ( planet != Planet.SATURN)
    {
      otherPoint = Planet.SATURN;
      Triple<Boolean , Point, Aspect> t = refranationImpl.resultOf(horoscopeContext, planet, otherPoint);
      //bean = new RefranationBean(horoscopeContext , planet , otherPoint , aspectApplySeparateImpl , relativeTransitImpl , retrogradeImpl);
      if (t.getFirst() == true)
      {
        //addComment(Locale.TAIWAN, planet + " 逃過了與 " + otherPoint + " 形成 " + bean.getApplyingAspect() + " (Refranation)");
        //return new Tuple<String , Object[]>("comment" , new Object[] {planet , otherPoint , bean.getApplyingAspect()} );
        return new Tuple<String , Object[]>("comment" , new Object[] {planet , otherPoint , t.getThird()} );
      }
    }
    return null;
  }

}
