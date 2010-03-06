/**
 * @author smallufo 
 * Created on 2008/1/2 at 下午 5:30:55
 */ 
package destiny.astrology.beans;

import java.io.Serializable;
import java.util.List;

import destiny.astrology.Aspect;
import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.Aspect.Importance;
import destiny.astrology.AspectApplySeparateIF.AspectType;

/**
 * <pre>
 * Translation of Light 的定義以及演算法 :
 * 根據此文的定義
 * http://destiny.xfiles.to/ubbthreads/ubbthreads.php/posts/6999
 * 「(1)某行星運行速度快於兩個徵象星, 且(2)該行星與一個徵象星有出相位, 與另一個有入相位, (3)而這兩個徵象星彼此是出相位」 
 * 
 * 目前演算法，先求出「夾」此星體的其他兩顆星體
 * 再比較三顆星的速度 , 如果中間的 planet 比較快 (至此已經符合第一個條件)
 * 
 * 再比較 planet 與 前後兩顆星是否分別形成入相位以及出相位
 * 
 * 最後，包夾此行星的兩顆星，是否要形成「出相位」，我認為「不一定」，因為意義不同，應該可以設為選項
 * </pre> 
 */
public class TranslationOfLightBean implements Serializable
{
  private BesiegedBean besiegedBean;
  private Planet planet;
  private HoroscopeContext horoscopeContext;
  
  /** 判斷入相位，或是出相位 */
  private AspectApplySeparateIF aspectApplySeparateImpl;
  
  /** 是否傳遞光線 */
  private boolean translatingLight = false;
  
  /** 從哪顆星傳遞 */
  private Planet fromPlanet = null;
  
  /** 傳遞到哪顆星 */
  private Planet toPlanet = null;
  
  /** 包夾的兩顆星 (fromPlanet / toPlanet) ，是否形成交角。如果沒有交角，則為 null ; 如果有交角，則該兩顆星是「入相位」還是「出相位」 */  
  private AspectType besigingPlanetsAspectType = null;
  
  public TranslationOfLightBean(Planet planet , 
                                HoroscopeContext horoscopeContext , 
                                RelativeTransitIF relativeTransitImpl , AspectApplySeparateIF aspectApplySeparateImpl )
  {
    this.besiegedBean = new BesiegedBean(relativeTransitImpl);
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.planet = planet;
    this.horoscopeContext = horoscopeContext;
    calculate();
  }
  
  /**
   * @param planet 計算此星是否有傳送光線
   * @param horoscopeContext
   * @return
   */
  private void calculate()
  {
    /** 不考慮合相的交角 */
    Aspect[] aspects = {Aspect.SEXTILE , Aspect.SQUARE , Aspect.TRINE , Aspect.OPPOSITION};
    List<Planet> twoPlanets = besiegedBean.getBesiegingPlanets(planet, horoscopeContext.getGmt() , true , aspects);
    if (twoPlanets.get(0) == null || twoPlanets.get(1) == null)
      return;
    Planet p1 = twoPlanets.get(0);
    Planet p2 = twoPlanets.get(1);
    double planetSpeed = horoscopeContext.getPosition(planet).getSpeedLongitude();
    double p1Speed = horoscopeContext.getPosition(p1).getSpeedLongitude();
    double p2Speed = horoscopeContext.getPosition(p2).getSpeedLongitude();
    
    //中間被夾的星，速度比較快
    if (Math.abs(planetSpeed) > Math.abs(p1Speed) && Math.abs(planetSpeed) > Math.abs(p2Speed))
    {
      AspectType planet_p1 = aspectApplySeparateImpl.getAspectType(horoscopeContext, planet , p1, Aspect.getAngles(Importance.HIGH));
      //System.out.println("planet_p1 = " + planet_p1);
      AspectType planet_p2 = aspectApplySeparateImpl.getAspectType(horoscopeContext, planet , p2, Aspect.getAngles(Importance.HIGH));
      //System.out.println("planet_p2 = " + planet_p2);
      
      //2008-11-05 新增規則： 第三顆星需與這兩顆星分別形成出相位以及入相位 
      if (planet_p1 != null && planet_p2 != null && planet_p1 != planet_p2)
      {
        this.translatingLight = true;
        this.fromPlanet = twoPlanets.get(0);
        this.toPlanet = twoPlanets.get(1);
        
        // 第三個規則：夾 planet 的兩顆星的交角 (besigingPlanetsAspectType) , 還必須是「出相位」
        // 我對此規則持保留態度，所以未採用，但會將此結果輸出
        this.besigingPlanetsAspectType = aspectApplySeparateImpl.getAspectType(horoscopeContext, p1 , p2 , Aspect.getAngles(Importance.HIGH));
      }
    }
  }

  public boolean isTranslatingLight()
  {
    return translatingLight;
  }

  public Planet getFromPlanet()
  {
    return fromPlanet;
  }

  public Planet getToPlanet()
  {
    return toPlanet;
  }

  public AspectType getBesigingPlanetsAspectType()
  {
    return besigingPlanetsAspectType;
  }
}
